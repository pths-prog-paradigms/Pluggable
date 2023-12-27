package org.ldemetrios.plugin

import org.ldemetrios.utils.Quadruple
import org.ldemetrios.utils.toHexAndHash

class Main {
    companion object {
        @JvmStatic
        fun function(x: Short): Short {
            return Quadruple(
                x.toHexAndHash(),
                (x * 5).toHexAndHash(),
                (x + 7).toHexAndHash(),
                (x / 11).toHexAndHash()
            ).sum().toShort() // Imagine something really complex happens here
        }
    }
}
