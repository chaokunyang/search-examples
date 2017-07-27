package com.timeyang.search.hibernate.example;

import lombok.*;
import org.hibernate.search.annotations.Field;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @author chaokunyang
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class Author {

    @Id
    @GeneratedValue
    private Integer id;

    @Field
    private String name;

}