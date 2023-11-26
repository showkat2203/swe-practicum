package com.baylor.practicum_new.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkUploadDTO {
    private List<UserProductInput> users;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserProductInput {
        private Long userId;
        private String userName;
        private List<ProductInput> products;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonDeserialize(using = ProductInputDeserializer.class)
    public static class ProductInput {
        private String productName;
        private String description;
        private Set<Long> categoryIds;
    }

}


//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class BulkUploadDTO {
//    private List<UserProductInput> users;
//
//    @Data
//    @NoArgsConstructor
//    @AllArgsConstructor
//    public static class UserProductInput {
//        private Long userId;
//        private String userName;
//        private List<ProductInput> products;
//    }
//
//    @Data
//    @NoArgsConstructor
//    @AllArgsConstructor
//    public static class ProductInput {
//        private String productName;
//        private String description;
//        private Set<Long> categoryIds;
//    }
//}
