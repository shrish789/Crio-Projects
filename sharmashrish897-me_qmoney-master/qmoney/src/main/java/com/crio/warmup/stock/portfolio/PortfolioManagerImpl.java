
package com.crio.warmup.stock.portfolio;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;

import java.time.Duration;

import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.exception.StockQuoteServiceException;
import com.crio.warmup.stock.quotes.StockQuotesService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerImpl implements PortfolioManager {

  private static final int UNIT_TIMEOUT = 6;
  private RestTemplate restTemplate;
  private StockQuotesService stockQuotesService;

  // Caution: Do not delete or modify the constructor, or else your build will
  // break!
  // This is absolutely necessary for backward compatibility
  @Deprecated
  protected PortfolioManagerImpl(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  protected PortfolioManagerImpl(StockQuotesService stockQuotesService) {
    this.stockQuotesService = stockQuotesService;
  }

  // CHECKSTYLE:OFF

  private Comparator<AnnualizedReturn> getComparator() {
    return Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
  }

  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to) throws JsonProcessingException {
    String url = buildUri(symbol, from, to);
    Candle[] results = restTemplate.getForObject(url, TiingoCandle[].class);
    List<Candle> candleList = Arrays.asList(results);
    return candleList;
  }

  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
    String token = "876f9f00db8f375707a5f1cac05e64e699e8c967";
    String uriTemplate = "https://api.tiingo.com/tiingo/daily/$SYMBOL/prices?"
        + "startDate=$STARTDATE&endDate=$ENDDATE&token=$APIKEY";
    return uriTemplate.replace("$APIKEY", token).replace("$SYMBOL", symbol)
        .replace("$STARTDATE", startDate.toString())
        .replace("$ENDDATE", endDate.toString());
  }

  @Override
  public List<AnnualizedReturn> calculateAnnualizedReturn(List<PortfolioTrade> portfolioTrades, LocalDate endDate)
      throws StockQuoteServiceException {
    // TODO Auto-generated method stub

    try {

      List<AnnualizedReturn> annualizedReturns = new ArrayList<AnnualizedReturn>();
      for (PortfolioTrade t : portfolioTrades) {
        List<Candle> candles = stockQuotesService.getStockQuote(t.getSymbol(), t.getPurchaseDate(), endDate);
        Double buyPrice = candles.get(0).getOpen();
        Double sellPrice = candles.get(candles.size() - 1).getClose();
        annualizedReturns.add(calculateAnnualizedReturns(endDate, t, buyPrice, sellPrice));
      }
      Collections.sort(annualizedReturns, getComparator());
      return annualizedReturns;
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Error occured while processing response from Tiingo");
    }

  }

  public static AnnualizedReturn calculateAnnualizedReturns(LocalDate endDate, PortfolioTrade trade, 
      Double buyPrice, Double sellPrice) {

    Double total_returns = (sellPrice - buyPrice) / buyPrice;
    Double total_num_years = (double) Duration.between(trade.getPurchaseDate().atStartOfDay(), 
        endDate.atStartOfDay()).toDays() / 365.0;
    Double annualized_returns = Math.pow((1 + total_returns), 1 / total_num_years) - 1;

    return new AnnualizedReturn(trade.getSymbol(), annualized_returns, total_returns);
  }

  @Override
  public List<AnnualizedReturn> calculateAnnualizedReturnParallel(List<PortfolioTrade> portfolioTrades,
      LocalDate endDate, int numThreads) throws InterruptedException, StockQuoteServiceException {
    ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
    List<Future<AnnualizedReturn>> responses = portfolioTrades.stream()
        .map(portfolioTrade -> executorService.submit(
              () -> getAnnualizedreturn(endDate, portfolioTrade)))
                  .collect(Collectors.toList());
    
    executorService.shutdown();
    executorService
        .awaitTermination(portfolioTrades.size() * UNIT_TIMEOUT / numThreads, TimeUnit.SECONDS);
    
    List<AnnualizedReturn> results = new ArrayList<>();
    for (Future<AnnualizedReturn> response : responses) {
      try {
        results.add(response.get());
      } catch (ExecutionException e) {
        throw new StockQuoteServiceException("Unable to get data from provider", e);
      }
    }

    return results.stream()
        .sorted(getComparator())
        .collect(Collectors.toList());
  }

  private AnnualizedReturn getAnnualizedreturn(LocalDate endDate, PortfolioTrade portfolioTrade)
      throws JsonProcessingException, StockQuoteServiceException {
    List<Candle> candles = stockQuotesService.getStockQuote(portfolioTrade.getSymbol(), 
        portfolioTrade.getPurchaseDate(), endDate);
    Double buyPrice = candles.get(0).getOpen();
    Double sellPrice = candles.get(candles.size() - 1).getClose();
    return calculateAnnualizedReturns(endDate, portfolioTrade, buyPrice, sellPrice);
  }
}
