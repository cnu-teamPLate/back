package com.cnu.teamProj.teamProj.config;

import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSBucket;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;

@Configuration
public class GridFsConfig {

    @Bean
    public GridFsTemplate gridFsTemplate(MongoDatabaseFactory dbFactory, MappingMongoConverter converter) {
        return new GridFsTemplate(dbFactory, converter);
    }

    @Bean
    public GridFSBucket gridFsBucket(MongoDatabaseFactory dbFactory) {
        return GridFSBuckets.create(dbFactory.getMongoDatabase());
    }
}
