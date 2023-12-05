package com.example.ammf_core.performance

import java.io.File

/**
 * This class is responsible for retrieving CPU usage information from the device.
 * Works up to Android <= 8.0
 */
class CpuUsageManager(
    private val activeTime: Long,
    private val totalTime: Long
) {
    companion object {
        fun getCpuUsage(): CpuUsage {
            // Execute ADB command to get CPU usage information
            val adbShellOutput = try {
                Runtime.getRuntime().exec("adb shell top -n 1")
                    .inputStream.bufferedReader().readText()
            } catch (e: Exception) {
                e.printStackTrace()
                return CpuUsage(0f, 0)
            }

            // Parse the output to extract active and total CPU times
            val regex = Regex("(?<=User CPU:\t)\\d+.\\d+")
            val matchResult = regex.find(adbShellOutput)

            return if (matchResult != null) {
                val cpuUsagePercentage = matchResult.value.replace("%", "").toFloat()

                // Retrieve total CPU time from /proc/stat file
                val totalTime = retrieveTotalCpuTime()

                val activeTime = (cpuUsagePercentage * totalTime) / 100
                CpuUsage(activeTime, totalTime)
            } else {
                CpuUsage(0f, 0)
            }
        }

        private fun retrieveTotalCpuTime(): Long {
            // Read the contents of /proc/stat file
            val statFileContents = try {
                File("/proc/stat").readText()
            } catch (e: Exception) {
                e.printStackTrace()
                return 0
            }

            // Extract total CPU time from the file contents
            val regex = Regex("cpu\\s+\\d+")
            val matchResults = regex.findAll(statFileContents)

            return if (matchResults.count() > 0) {
                val totalCpuTime = matchResults.map { it.value.toInt() }.sum()
                totalCpuTime.toLong()
            } else {
                0
            }
        }
    }
}
