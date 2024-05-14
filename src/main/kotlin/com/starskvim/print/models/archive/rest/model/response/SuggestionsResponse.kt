package com.starskvim.print.models.archive.rest.model.response

import com.starskvim.print.models.archive.rest.model.ptint_model.PrintModelSuggest

data class SuggestionsResponse(

    val suggestions: List<PrintModelSuggest>

)