package com.xuecheng.framework.domain.speed;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;


@Data
@ToString
@Entity
@Table(name="goods")
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
public class Goods implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private int count;
    private int sale;
    private int version;
}
