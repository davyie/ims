package com.ims.application.port.inbound;

import com.ims.application.dto.StorageSummaryDto;
import com.ims.application.query.GetStorageSummaryQuery;

public interface StorageSummaryPort {
    StorageSummaryDto getStorageSummary(GetStorageSummaryQuery query);
}
