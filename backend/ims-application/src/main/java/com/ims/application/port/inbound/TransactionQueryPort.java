package com.ims.application.port.inbound;

import com.ims.application.query.GetTransactionHistoryQuery;
import com.ims.domain.model.Transaction;

import java.util.List;

public interface TransactionQueryPort {
    List<Transaction> getTransactionHistory(GetTransactionHistoryQuery query);
}
