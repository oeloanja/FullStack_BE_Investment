package com.billit.investment.kafka.config;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

public class CustomJsonSerializer<T> implements Serializer<T> {
    @Override
    public byte[] serialize(String topic, T data) {
        // 타입 헤더를 제거하지 않거나, 필요하다면 명확히 타입 정보를 유지하도록 수정
        // 예시: ObjectMapper로 데이터를 JSON 문자열로 변환하면서 타입 정보를 추가로 명시
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (JsonProcessingException e) {
            throw new SerializationException("Error serializing value", e);
        }
    }
}