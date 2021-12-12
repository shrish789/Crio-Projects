
package com.crio.warmup.stock;

import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.dto.TotalReturnsDto;
import com.crio.warmup.stock.log.UncaughtExceptionHandler;
import com.crio.warmup.stock.dto.TotalReturnsDto;
import com.crio.warmup.stock.log.UncaughtExceptionHandler;
import com.crio.warmup.stock.portfolio.PortfolioManager;
import com.crio.warmup.stock.portfolio.PortfolioManagerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerApplication {

  // CRIO_TASK_MODULE_JSON_PARSING

  public static List<String> mainReadFile(final String[] args) throws IOException, URISyntaxException {

    final File inputFile = resolveFileFromResources(args[0]);
    // ObjectMapper objectMapper = new ObjectMapper();

    final PortfolioTrade[] portfolioTrades = getObjectMapper().readValue(inputFile, PortfolioTrade[].class);
    final List<String> symbolList = new ArrayList<>();
    for (final PortfolioTrade trade : portfolioTrades) {
      symbolList.add(trade.getSymbol());
    }

    return symbolList;
  }

  // CRIO_TASK_MODULE_REST_API

  private static void printJsonObject(final Object object) throws IOException {
    final Logger logger = Logger.getLogger(PortfolioManagerApplication.class.getCanonicalName());
    final ObjectMapper mapper = new ObjectMapper();
    logger.info(mapper.writeValueAsString(object));
  }

  private static File resolveFileFromResources(final String filename) throws URISyntaxException {
    return Paths.get(Thread.currentThread().getContextClassLoader().getResource(filename).toURI()).toFile();
  }

  private static ObjectMapper getObjectMapper() {
    final ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }

  // CRIO_TASK_MODULE_JSON_PARSING

  public static List<String> debugOutputs() {

    final String valueOfArgument0 = "trades.json";
    final String resultOfResolveFilePathArgs0 = "/home/crio-user/workspace/sharmashrish897-ME_QMONEY/qmoney/bin/main/trades.json";
    final String toStringOfObjectMapper = "com.fasterxml.jackson.databind.ObjectMapper@2f9f7dcf";
    final String functionNameFromTestFileInStackTrace = "PortfolioManagerApplicationTest.mainReadFile()";
    final String lineNumberFromTestFileInStackTrace = "22";

    return Arrays.asList(new String[] { valueOfArgument0, resultOfResolveFilePathArgs0, toStringOfObjectMapper,
        functionNameFromTestFileInStackTrace, lineNumberFromTestFileInStackTrace });
  }

  public static List<String> mainReadQuotes(final String[] args) throws IOException, URISyntaxException {

    final File inputFile = resolveFileFromResources(args[0]);
    final ObjectMapper objectMapper = getObjectMapper();

    final List<PortfolioTrade> trades = Arrays.asList(objectMapper.readValue(inputFile, PortfolioTrade[].class));
    final List<TotalReturnsDto> sortedByValue = mainReadQuotesHelper(args, trades);
    Collections.sort(sortedByValue, TotalReturnsDto.closingComparator);
    final List<String> stocks = new ArrayList<String>();
    for (final TotalReturnsDto trd : sortedByValue) {
      stocks.add(trd.getSymbol());
    }

    return stocks;
  }

  public static List<TotalReturnsDto> mainReadQuotesHelper(final String[] args, final List<PortfolioTrade> trades)
      throws IOException, URISyntaxException {
    final RestTemplate restTemplate = new RestTemplate();
    final List<TotalReturnsDto> tests = new ArrayList<TotalReturnsDto>();
    for (final PortfolioTrade t : trades) {
      final String url = "https://api.tiingo.com/tiingo/daily/" + t.getSymbol() + "/prices?startDate="
          + t.getPurchaseDate().toString() + "&endDate=" + args[1] + "&token=876f9f00db8f375707a5f1cac05e64e699e8c967";
      final TiingoCandle[] results = restTemplate.getForObject(url, TiingoCandle[].class);
      if (results != null) {
        tests.add(new TotalReturnsDto(t.getSymbol(), results[results.length - 1].getClose()));
      }
    }
    return tests;
  }

  // CRIO_TASK_MODULE_CALCULATIONS

  public static List<AnnualizedReturn> mainCalculateSingleReturn(String[] args) throws IOException, URISyntaxException {

    File inputFile = resolveFileFromResources(args[0]);

    ObjectMapper objectMapper = getObjectMapper();
    List<PortfolioTrade> trades = Arrays.asList(objectMapper.readValue(inputFile, PortfolioTrade[].class));

    List<AnnualizedReturn> annualizedReturns = mainCalculateSingleReturnHelper(args, trades);
    Collections.sort(annualizedReturns, AnnualizedReturn.descendingComparator);
    return annualizedReturns;
  }

  public static List<AnnualizedReturn> mainCalculateSingleReturnHelper(String[] args, List<PortfolioTrade> trades)
      throws IOException, URISyntaxException {
    RestTemplate restTemplate = new RestTemplate();
    LocalDate endDate = LocalDate.parse(args[1]);
    List<AnnualizedReturn> tests = new ArrayList<AnnualizedReturn>();
    for (PortfolioTrade t : trades) {
      String url = "https://api.tiingo.com/tiingo/daily/" + t.getSymbol() + "/prices?startDate="
          + t.getPurchaseDate().toString() + "&endDate=" + args[1] + "&token=876f9f00db8f375707a5f1cac05e64e699e8c967";
      TiingoCandle[] results = restTemplate.getForObject(url, TiingoCandle[].class);
      if (results != null) {
        Double buyPrice = results[0].getOpen();
        Double sellPrice = results[results.length - 1].getClose();
        tests.add(calculateAnnualizedReturns(endDate, t, buyPrice, sellPrice));
      }
    }

    return tests;
  }

  // CRIO_TASK_MODULE_CALCULATIONS

  public static AnnualizedReturn calculateAnnualizedReturns(LocalDate endDate, PortfolioTrade trade, Double buyPrice,
      Double sellPrice) {

    Double total_returns = (sellPrice - buyPrice) / buyPrice;
    Double total_num_years = (double) Duration.between(trade.getPurchaseDate().atStartOfDay(), endDate.atStartOfDay())
        .toDays() / 365.0;
    Double annualized_returns = Math.pow((1 + total_returns), 1 / total_num_years) - 1;

    return new AnnualizedReturn(trade.getSymbol(), annualized_returns, total_returns);
  }

  // TODO: CRIO_TASK_MODULE_REFACTOR
  // Once you are done with the implementation inside PortfolioManagerImpl and
  // PortfolioManagerFactory, create PortfolioManager using
  // PortfolioManagerFactory.
  // Refer to the code from previous modules to get the List<PortfolioTrades> and
  // endDate, and
  // call the newly implemented method in PortfolioManager to calculate the
  // annualized returns.

  // Note:
  // Remember to confirm that you are getting same results for annualized returns
  // as in Module 3.

  public static List<AnnualizedReturn> mainCalculateReturnsAfterRefactor(String[] args) throws Exception {
    File tradesFile = resolveFileFromResources(args[0]);
    PortfolioTrade[] trades = getObjectMapper().readValue(tradesFile, PortfolioTrade[].class);
    LocalDate endDate = LocalDate.parse(args[1]);
    PortfolioManager portfolioManager = PortfolioManagerFactory.getPortfolioManager((new RestTemplate()));
    return portfolioManager.calculateAnnualizedReturn(Arrays.asList(trades), endDate);
  }

  public static void main(String[] args) throws Exception {
    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
    ThreadContext.put("runId", UUID.randomUUID().toString());

    // printJsonObject(mainCalculateSingleReturn(args));

    printJsonObject(mainCalculateReturnsAfterRefactor(args));
  }
}
