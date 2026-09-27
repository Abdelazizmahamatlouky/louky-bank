package com.louky.bank.mapper;

import com.louky.bank.domain.entity.Notification;
import com.louky.bank.dto.response.NotificationResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    NotificationResponse toResponse(Notification notification);
}