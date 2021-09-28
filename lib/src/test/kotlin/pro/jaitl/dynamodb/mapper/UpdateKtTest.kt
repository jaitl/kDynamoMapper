package pro.jaitl.dynamodb.mapper

import pro.jaitl.dynamodb.mapper.attribute.stringAttribute
import pro.jaitl.dynamodb.mapper.attribute.updateAttribute
import software.amazon.awssdk.services.dynamodb.model.AttributeAction
import software.amazon.awssdk.services.dynamodb.model.AttributeValueUpdate
import kotlin.test.Test
import kotlin.test.assertEquals

internal class UpdateKtTest {
    @Test
    fun testPutUpdate() {
        val attr = stringAttribute("123")
        val update = updateAttribute(attr, AttributeAction.PUT)

        val expected =
            AttributeValueUpdate.builder().value(attr).action(AttributeAction.PUT).build()

        assertEquals(expected, update)
    }
}
