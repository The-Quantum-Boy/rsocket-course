package com.vinsguru.springrsocket;

import io.rsocket.transport.netty.client.TcpClientTransport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class Lec04CallbackTest {

    @Autowired
    private RSocketRequester requester;

    @Autowired
    private RSocketRequester.Builder builder;

    @Autowired
    private RSocketMessageHandler handler;



    @BeforeAll
    public void setup(){
        RSocketMessageHandler handler1=new RSocketMessageHandler();


        RSocketStrategies strategies=rSocketStrategies();
        this.requester = RSocketRequester
                .builder()
                .dataMimeType(MimeType.valueOf(MimeTypeUtils.ALL_VALUE))
                .rsocketConnector(c->c.acceptor(handler1.responder())) //as acceptor in simple rsocket
                .rsocketStrategies(strategies)
                .transport(TcpClientTransport.create("localhost", 6565));

    }

    public RSocketStrategies rSocketStrategies() {
        return RSocketStrategies.builder()
                .encoders(encoders -> encoders.add(new Jackson2JsonEncoder()))
                .decoders(decoders -> decoders.add(new Jackson2JsonDecoder()))
                .build();
    }


    @Test
    public void callbackTest() throws InterruptedException {
        Mono<Void> mono = this.requester.route("batch.job.request").data(5).send();

        StepVerifier.create(mono)
                .verifyComplete();

        Thread.sleep(12000);

    }


}
