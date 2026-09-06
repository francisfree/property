package com.castle.property.dto;

import java.util.List;
import java.util.UUID;

public record ReceiptsRequest(
        List<UUID> publicIds
) {
}