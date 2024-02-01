package com.indoornav.ui.screens.customerflow

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.indoornav.R

@Composable
fun GenericTopBar(
   @DrawableRes leftIcon: Int = R.drawable.left_arrow,
    onBackButtonClick: () -> Unit,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(id = leftIcon),
                modifier = Modifier
                    .padding(end =12.dp )
                    .clickable {
                        onBackButtonClick()
                    },
                tint = Color.Unspecified,
                contentDescription = null
            )

        }

    }
}