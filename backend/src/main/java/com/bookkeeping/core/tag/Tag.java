package com.bookkeeping.core.tag;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tags")
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Tag {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long userId;
    
    @Column(nullable = false)
    private String name;
    
    @Column(length = 7)
    private String color;
    
    @Column(name = "created_unix_time", nullable = false)
    private Long createdTime;
    
    @Column(name = "updated_unix_time")
    private Long updatedTime;
    
    @Column(name = "deleted")
    private Boolean deleted = false;
}