package com.starskvim.print.models.archive.rest.model.response

import com.starskvim.print.models.archive.rest.model.PageInfo
import com.starskvim.print.models.archive.rest.model.ptint_model.PrintModelCard

data class PrintModelCardsResponse(

    val models: List<PrintModelCard>,
    override val totalElements: Long,
    override val totalPages: Int

) : PageInfo(totalElements, totalPages)