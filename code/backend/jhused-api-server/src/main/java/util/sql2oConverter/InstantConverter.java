package util.sql2oConverter;

import org.sql2o.converters.Converter;
import org.sql2o.converters.ConverterException;

import java.time.Instant;

public class InstantConverter implements Converter<Instant> {
  @Override
  public Instant convert(final Object val) throws ConverterException {
    if (val instanceof java.sql.Timestamp) {
      return ((java.sql.Timestamp) val).toInstant();
    } else {
      return null;
    }
  }

  @Override
  public Object toDatabaseParam(final Instant val) {
    if (val == null) {
      return null;
    } else {
      return new java.sql.Timestamp(val.toEpochMilli());
    }
  }
}
