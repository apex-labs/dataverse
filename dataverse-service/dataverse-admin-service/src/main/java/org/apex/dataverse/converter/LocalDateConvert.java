package org.apex.dataverse.converter;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * @author: zoubin
 * @date: 11/23/18 14:02
 * @description: <LocalDateTime, Date> 之间转换器
 */
public class LocalDateConvert extends BidirectionalConverter<LocalDateTime, Date> {

    @Override
    public Date convertTo(LocalDateTime source, Type<Date> destinationType, MappingContext mappingContext) {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = source.atZone(zoneId);
        Date date = Date.from(zdt.toInstant());
        return date;
    }

    @Override
    public LocalDateTime convertFrom(Date source, Type<LocalDateTime> destinationType, MappingContext mappingContext) {
        ZoneId zoneId = ZoneId.systemDefault();
        Instant instant = source.toInstant();
        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
        return localDateTime;
    }
}
