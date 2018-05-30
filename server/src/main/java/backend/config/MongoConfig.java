package backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.MongoCredential;
import java.util.Collections;

@Configuration
@EnableMongoRepositories
@PropertySource("classpath:application.properties")
public class MongoConfig extends AbstractMongoConfiguration {

    @Value("${spring.data.mongodb.host}") String host;
    @Value("${spring.data.mongodb.port}") int port;
    @Value("${spring.data.mongodb.username}") String username;
    @Value("${spring.data.mongodb.password}") String password;
    @Value("${spring.data.mongodb.database}") String database;
    @Value("${spring.data.mongodb.authentication-database}") String authdatabase;

    @Override
    protected String getDatabaseName() {
        return this.database;
    }

    @Override
    protected String getMappingBasePackage() {
        return "backend.config";
    }

    @Override
    @Bean
    public MongoClient mongoClient() {
    	return new MongoClient(
                Collections.singletonList(new ServerAddress(this.host, this.port)),
                Collections.singletonList(MongoCredential.createCredential(this.username, this.authdatabase, this.password.toCharArray()))
            );
    }
    @Bean
    public MongoDbFactory mongoDbFactory() {
        return new SimpleMongoDbFactory(
            new MongoClient(
                Collections.singletonList(new ServerAddress(this.host, this.port)),
                Collections.singletonList(MongoCredential.createCredential(this.username, this.authdatabase, this.password.toCharArray()))
            ), getDatabaseName());
    }

    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        final MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory());
        return mongoTemplate;
    }
}