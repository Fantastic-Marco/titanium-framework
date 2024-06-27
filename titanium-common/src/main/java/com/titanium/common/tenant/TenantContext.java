package com.titanium.common.tenant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TenantContext {
    /**
     * 租户ID
     */
    private Long tenantId;

}
