package com.itheima.netty.protobuf;

import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.InvalidProtocolBufferException;
import com.itheima.netty.protobuf.SubscribeReqProto.SubscribeReq;

public class TestSubscribeReqProto {

    // 编码
    private static byte[] encode(SubscribeReq req) {

        return req.toByteArray();
    }

    // 解码
    private static SubscribeReq decode(byte[] body) throws InvalidProtocolBufferException {

        return SubscribeReq.parseFrom(body);
    }

    private static SubscribeReq createSubscribeReqProto() {

        SubscribeReq.Builder builder = SubscribeReq.newBuilder();
        builder.setSubReqID(1);
        builder.setUsername("Lilinfeng");
        builder.setProductName("Netty book");
        List<String> address = new ArrayList<String>();
        address.add("beijing");
        address.add("shanghai");
        address.add("shengzheng");
        builder.addAllAddress(address);
        return builder.build();
    }

    public static void main(String[] args) throws InvalidProtocolBufferException {

        SubscribeReq req = createSubscribeReqProto();
        System.out.println("Before encode :" + req.toString());
        SubscribeReq req2 = decode(encode(req));
        System.out.println("After decode :" + req2.toString());
        System.out.println("Assert equal:---> " + req2.equals(req));
    }
}
