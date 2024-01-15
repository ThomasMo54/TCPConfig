package com.motompro.tcpconfig.app.util

import com.motompro.tcpconfig.app.util.IPRange
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TestIPRange {

    @Test
    fun testStartEnd() {
        val ipRange1 = IPRange("192.168.0.1", "192.168.0.2")
        assertEquals("192.168.0.1", ipRange1.startIP)
        assertEquals("192.168.0.2", ipRange1.endIP)

        val ipRange2 = IPRange("192.168.0.2", "192.168.0.1")
        assertEquals("192.168.0.1", ipRange2.startIP)
        assertEquals("192.168.0.2", ipRange2.endIP)
    }

    @Test
    fun testIsValid() {
        val ipRange1 = IPRange("192.168.0.1", "192.168.0.2")
        assertTrue(ipRange1.isValid)

        val ipRange2 = IPRange("abc", "192.168.0.1")
        assertFalse(ipRange2.isValid)

        val ipRange3 = IPRange("192.168.0.1", "256.168.0.1")
        assertFalse(ipRange3.isValid)
    }

    @Test
    fun testIpList() {
        // Test only one address
        val ipRange1 = IPRange("192.168.0.1", "192.168.0.1")
        assertEquals(1, ipRange1.ipList.size)
        assertEquals("192.168.0.1", ipRange1.ipList[0])

        // Test multiple addresses
        val ipRange2 = IPRange("192.168.0.1", "192.168.0.3")
        assertEquals(3, ipRange2.ipList.size)
        assertEquals("192.168.0.1", ipRange2.ipList[0])
        assertEquals("192.168.0.2", ipRange2.ipList[1])
        assertEquals("192.168.0.3", ipRange2.ipList[2])

        // Test *.*.*.0 and *.*.*.255 exclusion
        val ipRange3 = IPRange("192.168.0.254", "192.168.1.1")
        assertEquals(2, ipRange3.ipList.size)
        assertEquals("192.168.0.254", ipRange3.ipList[0])
        assertEquals("192.168.1.1", ipRange3.ipList[1])
    }

    @Test
    fun testIsIPSmallerThanOther() {
        assertTrue(IPRange.isIPSmallerThanOther("192.168.0.1", "192.168.0.2"))
        assertFalse(IPRange.isIPSmallerThanOther("192.168.0.5", "192.168.0.4"))
        assertFalse(IPRange.isIPSmallerThanOther("192.168.0.8", "192.168.0.8"))
    }
}