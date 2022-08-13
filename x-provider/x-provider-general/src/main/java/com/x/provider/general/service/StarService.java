package com.x.provider.general.service;


import com.x.provider.api.general.model.dto.ListStarRequestDTO;
import com.x.provider.api.general.model.dto.StarRequestDTO;
import com.x.provider.general.model.domain.Star;

import java.util.List;

public interface StarService {
    boolean star(StarRequestDTO starAO);
    boolean isStarred(int itemType, long itemId, long customerId);
    List<Star> listStar(ListStarRequestDTO listStarAO);
}
