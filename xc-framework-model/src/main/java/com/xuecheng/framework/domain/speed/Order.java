package com.xuecheng.framework.domain.speed;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


@Data
@ToString
@Entity
@Table(name="speed_order")
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
public class Order implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String custname;
    private Date speed_create_time;
}