package at;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

//import org.springframework.jms.annotation.EnableJms;
//import org.springframework.jms.annotation.JmsListener;
//import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
//import org.springframework.jms.config.JmsListenerContainerFactory;
//import javax.jms.ConnectionFactory;
//import java.io.IOException;


//@EnableJms
@Configuration
@ComponentScan
public class CucumberTestConfig {

//    private static final String QUEUE_NAME = "testQueue";

//    @Bean
//    public JmsListenerContainerFactory<?> myFactory(ConnectionFactory connectionFactory, DefaultJmsListenerContainerFactoryConfigurer configurer) {
//        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
//        configurer.configure(factory, connectionFactory);
//        return factory;
//    }
//
//    @JmsListener(destination = QUEUE_NAME, containerFactory = "myFactory")
//    public void receiveMessage(String message) throws JSONException, IOException, InterruptedException {
//        String fieldToReplace = "onQueue";
//        setField(StepDefinitions.class, fieldToReplace, message);
//    }

}
