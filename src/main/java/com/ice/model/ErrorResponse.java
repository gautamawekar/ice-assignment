package com.ice.model;

import java.util.List;

public record ErrorResponse(List<Error> errors) {
}
