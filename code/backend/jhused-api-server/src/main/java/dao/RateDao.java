package dao;

import exceptions.DaoException;
import model.Rate;

import java.util.List;

public interface RateDao {

  Rate create(Rate rate) throws DaoException;

  Rate createOrUpdate(String raterId, String sellerId, Rate rate) throws DaoException;

  Rate read(String raterId, String sellerId) throws DaoException;

  List<Rate> read(String sellerId) throws DaoException;

  Rate update(String raterId, String sellerId, Rate rate) throws DaoException;

  Rate delete(String raterId, String sellerId) throws DaoException;
}
