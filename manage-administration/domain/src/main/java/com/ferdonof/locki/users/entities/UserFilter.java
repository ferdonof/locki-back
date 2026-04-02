package com.ferdonof.locki.users.entities;

import lombok.Builder;

@Builder
public record UserFilter(int limit, int offset) {
}

