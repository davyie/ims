package com.ims.application.usecase.market;

import com.ims.application.dto.AllMarketsSummaryDto;
import com.ims.application.dto.MarketItemSummaryDto;
import com.ims.application.dto.MarketSummaryDto;
import com.ims.application.port.inbound.MarketQueryPort;
import com.ims.application.query.GetAllMarketsSummaryQuery;
import com.ims.application.query.GetMarketQuery;
import com.ims.application.query.GetMarketSummaryQuery;
import com.ims.application.query.ListMarketsQuery;
import com.ims.domain.exception.MarketNotFoundException;
import com.ims.domain.model.Item;
import com.ims.domain.model.Market;
import com.ims.domain.model.MarketItem;
import com.ims.domain.model.Transaction;
import com.ims.domain.model.TransactionType;
import com.ims.domain.port.ItemRepositoryPort;
import com.ims.domain.port.MarketItemRepositoryPort;
import com.ims.domain.port.MarketRepositoryPort;
import com.ims.domain.port.TransactionRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class MarketQueryUseCase implements MarketQueryPort {

    private final MarketRepositoryPort marketRepository;
    private final MarketItemRepositoryPort marketItemRepository;
    private final ItemRepositoryPort itemRepository;
    private final TransactionRepositoryPort transactionRepository;

    public MarketQueryUseCase(MarketRepositoryPort marketRepository,
                               MarketItemRepositoryPort marketItemRepository,
                               ItemRepositoryPort itemRepository,
                               TransactionRepositoryPort transactionRepository) {
        this.marketRepository = marketRepository;
        this.marketItemRepository = marketItemRepository;
        this.itemRepository = itemRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Market getMarket(GetMarketQuery query) {
        return marketRepository.findById(query.marketId())
                .orElseThrow(() -> new MarketNotFoundException(query.marketId()));
    }

    @Override
    public List<Market> listMarkets(ListMarketsQuery query) {
        return marketRepository.findAll(query.status());
    }

    @Override
    public MarketSummaryDto getMarketSummary(GetMarketSummaryQuery query) {
        Market market = marketRepository.findById(query.marketId())
                .orElseThrow(() -> new MarketNotFoundException(query.marketId()));

        List<MarketItem> marketItems = marketItemRepository.findAllByMarketId(market.getId());
        List<MarketItemSummaryDto> itemSummaries = new ArrayList<>();
        BigDecimal totalRevenue = BigDecimal.ZERO;
        int totalAllocated = 0;
        int totalCurrent = 0;
        int totalSold = 0;
        String currency = "SEK";

        for (MarketItem mi : marketItems) {
            Optional<Item> optItem = itemRepository.findById(mi.getItemId());
            String itemName = optItem.map(Item::getName).orElse("Unknown");
            String sku = optItem.map(Item::getSku).orElse("Unknown");

            List<Transaction> sales = transactionRepository.findByMarketIdAndItemId(market.getId(), mi.getItemId())
                    .stream().filter(t -> t.getType() == TransactionType.SALE).toList();

            int sold = sales.stream().mapToInt(t -> -t.getQuantityDelta()).sum();
            if (mi.getMarketPrice() != null) {
                currency = mi.getMarketPrice().currency();
                BigDecimal itemRevenue = mi.getMarketPrice().amount().multiply(BigDecimal.valueOf(sold));
                totalRevenue = totalRevenue.add(itemRevenue);
                itemSummaries.add(new MarketItemSummaryDto(
                    mi.getItemId(), itemName, sku,
                    mi.getAllocatedStock().quantity(), mi.getCurrentStock().quantity(),
                    sold, itemRevenue, currency
                ));
            } else {
                itemSummaries.add(new MarketItemSummaryDto(
                    mi.getItemId(), itemName, sku,
                    mi.getAllocatedStock().quantity(), mi.getCurrentStock().quantity(),
                    sold, BigDecimal.ZERO, currency
                ));
            }
            totalAllocated += mi.getAllocatedStock().quantity();
            totalCurrent += mi.getCurrentStock().quantity();
            totalSold += sold;
        }

        return new MarketSummaryDto(market.getId(), market.getName(), marketItems.size(),
            totalAllocated, totalCurrent, totalSold, totalRevenue, currency, itemSummaries);
    }

    @Override
    public AllMarketsSummaryDto getAllMarketsSummary(GetAllMarketsSummaryQuery query) {
        List<Market> markets = marketRepository.findAll(query.status());
        List<MarketSummaryDto> summaries = markets.stream()
                .map(m -> getMarketSummary(new GetMarketSummaryQuery(m.getId())))
                .toList();

        BigDecimal totalRevenue = summaries.stream()
                .map(MarketSummaryDto::totalRevenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        int totalSold = summaries.stream().mapToInt(MarketSummaryDto::totalSold).sum();
        String currency = summaries.isEmpty() ? "SEK" : summaries.get(0).currency();

        return new AllMarketsSummaryDto(markets.size(), totalSold, totalRevenue, currency, summaries);
    }
}
