package com.iori.util;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.common.utils.AddressUtils;
import com.alibaba.otter.canal.protocol.CanalEntry.*;
import com.alibaba.otter.canal.protocol.Message;
import com.iori.client.ItemFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.util.List;

@Component
public class CanalUtil {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ItemFeignClient itemFeignClient;


    public void main() {
        System.out.println("开启同步");
        // 创建链接
        CanalConnector connector = CanalConnectors.newSingleConnector(new InetSocketAddress(AddressUtils.getHostIp(),
                11111), "example", "", "");
        int batchSize = 1000;
        try {
            //创建连接
            connector.connect();
            //监听mysql所有的库和表
            connector.subscribe(".*\\..*");
            //回滚到未进行ack的地方，下次fetch的时候，可以从最后一个没有ack的地方开始拿
            connector.rollback();
            boolean flag = true;
            while (flag) {
                Message message = connector.getWithoutAck(batchSize); // 获取指定数量的数据
                long batchId = message.getId();
                int size = message.getEntries().size();
                //用户没有更改数据库中的数据
                if (batchId == -1 || size == 0) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    //获取修改的每一条记录
                    printEntry(message.getEntries());
                }
                connector.ack(batchId); // 提交确认
            }
        } finally {
            connector.disconnect();
        }
    }




    private void printEntry(List<Entry> entrys) {
        for (Entry entry : entrys) {
            //检查到当前执行的代码是事物操作， 跳转下次
            if (entry.getEntryType() == EntryType.TRANSACTIONBEGIN || entry.getEntryType() == EntryType.TRANSACTIONEND) {
                continue;
            }

            //代码固定，获取rowChage对象
            RowChange rowChage = null;
            try {
                rowChage = RowChange.parseFrom(entry.getStoreValue());
            } catch (Exception e) {
                throw new RuntimeException("ERROR ## parser of eromanga-event has an error , data:" + entry.toString(),
                        e);
            }

            //rowChage getEventType 获取事件类型对象
            EventType eventType = rowChage.getEventType();
            System.out.println(String.format("================> binlog[%s:%s] , name[%s,%s] , eventType : %s",
                    entry.getHeader().getLogfileName(), entry.getHeader().getLogfileOffset(),
                    entry.getHeader().getSchemaName(), entry.getHeader().getTableName(),
                    eventType));

            if ("shop_content".equals(entry.getHeader().getSchemaName()) && "tb_content".equals(entry.getHeader().getTableName())) {
                this.contentTable(rowChage, eventType);
            }

            if ("shop_goods".equals(entry.getHeader().getSchemaName()) && "tb_sku".equals(entry.getHeader().getTableName())) {
                this.goodsTable(rowChage, eventType);
            }

        }
    }


    private void goodsTable(RowChange rowChage, EventType eventType) {
        for (RowData rowData : rowChage.getRowDatasList()) {
            if (eventType == EventType.DELETE) {
                //rowData.getBeforeColumnsList()获取删除之前的数据
                printHtml(rowData.getBeforeColumnsList());
            } else if (eventType == EventType.INSERT) {
                //rowData.getAfterColumnsList()获取添加之后的数据
                 printHtml(rowData.getAfterColumnsList());
            } else {
                //获取修改之后的数据
                System.out.println("-------> after");
                printHtml(rowData.getAfterColumnsList());
            }
        }
    }

    /**
     *  shop_content库下的 tb_content表
     * @param rowChage
     * @param eventType
     */
    private void contentTable(RowChange rowChage, EventType eventType) {
        for (RowData rowData : rowChage.getRowDatasList()) {
            if (eventType == EventType.DELETE) {
                //rowData.getBeforeColumnsList()获取删除之前的数据
                printColumn(rowData.getBeforeColumnsList());
            } else if (eventType == EventType.INSERT) {
                //rowData.getAfterColumnsList()获取添加之后的数据
                printColumn(rowData.getAfterColumnsList());
            } else {
                //获取修改之后的数据
                System.out.println("-------> after");
                printColumn(rowData.getAfterColumnsList());
            }
        }
    }

    /*
            for (RowData rowData : rowChage.getRowDatasList()) {
        if (eventType == EventType.DELETE) {
            //rowData.getBeforeColumnsList()获取删除之前的数据
            printColumn(rowData.getBeforeColumnsList());
        } else if (eventType == EventType.INSERT) {
            //rowData.getAfterColumnsList()获取添加之后的数据
            printColumn(rowData.getAfterColumnsList());
        } else {
            //获取修改之前的数据
            System.out.println("-------> before");
            printColumn(rowData.getBeforeColumnsList());
            //获取修改之后的数据
            System.out.println("-------> after");
            printColumn(rowData.getAfterColumnsList());
        }
    }
    */


    /**
     * 远程调用 如果添加或修改了数据 重新生成html页面
     * @param columns
     */
    private void printHtml(List<Column> columns) {
        for (Column column : columns) {
            System.out.println(column.getName() + " : " + column.getValue() + "  update=" + column.getUpdated());
            if (("id".equals(column.getName()))) {
                String id = column.getValue();
                //String result = restTemplate.getForObject("http://localhost:7310/item/create/" + id, String.class);
                String result = itemFeignClient.create(id);
                System.out.println(result);
                break;
            }
        }
    }

    /**
     * 将数据缓存到redis
     * @param columns
     */
    private void printColumn(List<Column> columns) {
        for (Column column : columns) {
            System.out.println(column.getName() + " : " + column.getValue() + "  update=" + column.getUpdated());
            if (("category_id".equals(column.getName()))) {
                String cateId = column.getValue();
                String result = restTemplate.getForObject("http://localhost:9090/web07?cid=" + cateId, String.class);
                System.out.println(result);
                break;
            }
        }
    }
}