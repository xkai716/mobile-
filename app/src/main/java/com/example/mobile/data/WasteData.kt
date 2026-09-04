package com.example.mobile.data

import com.example.mobile.R
import com.example.mobile.model.WasteCategory
import com.example.mobile.model.WasteItem

object WasteData {

    val wasteList = listOf(
        WasteItem(
            id = 1,
            name = "Plastic drink bottle",
            category = WasteCategory.PLASTIC,
            disposalType = "Clean recyclable collection",
            recyclable = true,
            description = "Empty and lightly rinse the bottle before collection.",
            steps = listOf(
                "Empty all liquid from the bottle.",
                "Lightly rinse it so food or drink does not contaminate other recyclables.",
                "Follow the cap and label instructions of your local collector.",
                "Place it in the correct recycling stream or bring it to a collection point."
            ),
            photoResId = R.drawable.plastic_bottle,
            reminder = "Local collectors may accept different plastic resin types.",
        ),
        WasteItem(
            id = 2,
            name = "Cardboard box",
            category = WasteCategory.PAPER,
            disposalType = "Paper recycling",
            recyclable = true,
            description = "Keep cardboard clean and dry, then flatten it.",
            steps = listOf(
                "Remove leftover food and non-paper packing material.",
                "Keep the cardboard dry.",
                "Flatten the box to save space.",
                "Place it in paper recycling or take it to a collection point."
            ),
            photoResId = R.drawable.cardbox,
        ),
        WasteItem(
            id = 3,
            name = "Glass bottle or jar",
            category = WasteCategory.GLASS,
            disposalType = "Glass collection",
            recyclable = true,
            description = "Empty, rinse, and separate it from ceramics and mirrors.",
            steps = listOf(
                "Empty and lightly rinse the container.",
                "Remove loose lids or caps if your collector asks for this.",
                "Keep bottles and jars separate from ceramics, mirrors, and drinking glasses.",
                "Bring it to a collector that accepts glass."
            ),
            photoResId = R.drawable.glass,
            reminder = "Wrap broken glass safely and check the collector's instructions before transport.",
        ),
        WasteItem(
            id = 4,
            name = "Aluminium drink can",
            category = WasteCategory.METAL,
            disposalType = "Metal recycling",
            recyclable = true,
            description = "Empty and rinse the can before recycling.",
            steps = listOf(
                "Empty all liquid.",
                "Lightly rinse the can.",
                "Allow it to dry.",
                "Place it in the accepted metal-recycling stream."
            ),
            photoResId = R.drawable.can,
        ),
        WasteItem(
            id = 5,
            name = "Fruit and vegetable scraps",
            category = WasteCategory.ORGANIC,
            disposalType = "Compost or organic-waste collection",
            recyclable = true,
            description = "Compost suitable food scraps when a safe system is available.",
            steps = listOf(
                "Remove plastic stickers, bags, and packaging.",
                "Separate suitable plant-based scraps.",
                "Add them to a managed compost bin or approved organic-waste collection.",
                "Keep the compost balanced and follow the system's food-scrap rules."
            ),
            photoResId = R.drawable.fruit_vegetables,
            reminder = "Do not add unsuitable food to a compost system that cannot manage it.",
        ),
        WasteItem(
            id = 6,
            name = "Mobile phone",
            category = WasteCategory.E_WASTE,
            disposalType = "Registered household e-waste collector",
            recyclable = true,
            description = "Back up and erase personal data, then use an authorised channel.",
            steps = listOf(
                "Back up information that you need.",
                "Sign out and erase personal data when possible.",
                "Remove the SIM and memory card.",
                "Send the phone to a DOE-registered collection centre or authorised collector."
            ),
            photoResId = R.drawable.phone,
            reminder = "Do not burn e-waste or place it in ordinary household recycling.",
        ),
        WasteItem(
            id = 7,
            name = "Laptop or computer",
            category = WasteCategory.E_WASTE,
            disposalType = "Registered household e-waste collector",
            recyclable = true,
            description = "Protect your data and send the equipment through a formal channel.",
            steps = listOf(
                "Back up required files.",
                "Sign out and securely erase personal data.",
                "Keep loose batteries protected from short circuits.",
                "Use a DOE-registered collection centre or licensed recovery facility."
            ),
            photoResId = R.drawable.laptop,
            reminder = "Do not dismantle damaged batteries yourself.",
        ),
        WasteItem(
            id = 8,
            name = "Paper coffee cup",
            category = WasteCategory.PAPER,
            disposalType = "Check local acceptance",
            recyclable = false,
            description = "Many paper cups contain a lining and are not accepted with ordinary paper.",
            steps = listOf(
                "Empty the cup.",
                "Separate the lid and sleeve when possible.",
                "Check whether a nearby collector specifically accepts lined cups.",
                "If no suitable service accepts it, use the local residual-waste stream."
            ),
            photoResId = R.drawable.papercup,
            reminder = "Do not assume every item that looks like paper belongs in paper recycling.",
        )
    )

    fun getById(id: Int): WasteItem? =
        wasteList.find { it.id == id }
}
