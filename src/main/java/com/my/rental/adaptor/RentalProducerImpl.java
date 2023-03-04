package com.my.rental.adaptor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.rental.config.KafkaProperties;
import com.my.rental.domain.event.BookCatalogChanged;
import com.my.rental.domain.event.PointChanged;
import com.my.rental.domain.event.StockChanged;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutionException;

@Service
public class RentalProducerImpl implements RentalProducer{
    private final Logger log = LoggerFactory.getLogger(RentalProducer.class);

    //토픽명
    private static final String TOPIC_BOOK = "topic_book";
    private static final String TOPIC_CATALOG = "topic_catalog";
    private static final String TOPIC_POINT = "topic_point";
    /*
    * (1) 카프카의 메시지 교환 통로가 되는 토픽은 별도의 설정 없이 발행하는 쪽과
    * 받는 쪽의 토픽을 통일하게 맞추면 각 토픽에 해당하는 메시지를 받게 된다.
    *
    * */

    private final KafkaProperties kafkaProperties;
    private final static Logger logger = LoggerFactory.getLogger(RentalProducer.class);
    private KafkaProducer<String, String> producer;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RentalProducerImpl(KafkaProperties kafkaProperties){
        this.kafkaProperties = kafkaProperties;
    }


    @PostConstruct // 의존성 주입이 이루어진 후 초기화를 수행하는 메서드
    public void initialize(){
        log.info("Kafka producer initializing...");
        this.producer = new KafkaProducer<>(kafkaProperties.getProducerProps());
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
        log.info("Kafka producer initialized");
    }

    /******
     * kafka 메세지 수신 후, 결과 메세지 받도록 변경
     *
     * *******/

    //도서 서비스의 도서 상태 변경에 대한 카프카 메시지 발행
    @Override
    public void updateBookStatus(Long bookId, String bookStatus) throws ExecutionException, InterruptedException, JsonProcessingException {
        StockChanged stockChanged = new StockChanged(bookId, bookStatus); //(2) StockChanged 라는 도메인이벤트 생성
        String message = objectMapper.writeValueAsString(stockChanged);
        producer.send(new ProducerRecord<>(TOPIC_BOOK, message)).get();//(3) 메시지 전송 할때는 objectMapper로 해당 도메인 이벤트를 문자열 메시지 형태로 변경하여 ProducerRecord 에 담아서 전송(발행)
    }

    //사용자 서비스의 포인트 적입에 대한 카프카 메시지 발행
    @Override
    public void savePoints(Long userId, int points) throws ExecutionException, InterruptedException, JsonProcessingException {
        PointChanged pointChanged = new PointChanged(userId, points);//(4) pointChanged 이벤트 생성
        String message = objectMapper.writeValueAsString(pointChanged);
        producer.send(new ProducerRecord<>(TOPIC_POINT, message)).get();//(5) 메시지 전송 할때는 objectMapper로 해당 도메인 이벤트를 문자열 메시지 형태로 변경하여 ProducerRecord 에 담아서 전송(발행)
    }

    //도서 카탈로그 서비스의 도서 상태 변경에 대한 카프카 메시지 발행
    @Override
    public void updateBookCatalogStatus(Long bookId, String eventType) throws ExecutionException, InterruptedException, JsonProcessingException {
        BookCatalogChanged bookCatalogChanged = new BookCatalogChanged();//(6) BookCatalogChanged 이벤트 생성
        bookCatalogChanged.setBookId(bookId);
        bookCatalogChanged.setEventType(eventType);
        String message = objectMapper.writeValueAsString(bookCatalogChanged);
        producer.send(new ProducerRecord<>(TOPIC_CATALOG, message)).get();//(7) 메시지 전송 할때는 objectMapper로 해당 도메인 이벤트를 문자열 메시지 형태로 변경하여 ProducerRecord 에 담아서 전송(발행)
    }

    @PreDestroy
    public void shutdown(){
        log.info("Shutdown Kafka producer");
        producer.close();
    }
}
