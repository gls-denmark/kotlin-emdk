@file:OptIn(ExperimentalCoroutinesApi::class)

package dk.gls.kotlin_emdk

import dk.gls.kemdk.EMDKResult
import dk.gls.kemdk.deviceInformation.DeviceInformation
import dk.gls.kemdk.emdk.EMDKIntegration
import dk.gls.kemdk.emdk.EMDKThrowable
import dk.gls.kemdk.model.OEMInfo
import dk.gls.kemdk.model.RetryConfiguration
import dk.gls.kemdk.oemInfo.OEMInfoRequester
import dk.gls.kemdk.oemInfo.OEMInfoThrowable
import dk.gls.kemdk.toKEMDKFailure
import dk.gls.kemdk.toKEMDKSuccess
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class DeviceInformationTest {

    @Test
    fun `configure is correctly retried after first attempt to retrieve fails`() = runTest {
        //Arrange
        val emdkIntegration = mockk<EMDKIntegration>(relaxed = true) {
            coEvery {
                configure()
            } returns EMDKThrowable.ProfileLoadThrowable(Throwable("Could not set the profile")).toKEMDKFailure() andThen Unit.toKEMDKSuccess()
        }
        val oemInfoRequester = mockk<OEMInfoRequester>(relaxed = true)

        val sut = DeviceInformation(
            emdkIntegration = emdkIntegration,
            oemInfoRequester = oemInfoRequester,
            retryConfiguration = RetryConfiguration()
        )

        //Act
        val resultList = mutableListOf<EMDKResult<Unit, EMDKThrowable>>()

        launch {
            sut.configure().take(2).toList(resultList)
        }

        advanceUntilIdle()

        //Assert
        Assert.assertTrue(resultList.first() is EMDKResult.Failure)

        Assert.assertTrue(resultList[1] is EMDKResult.Success)
    }

    @Test
    fun `fetching oem info is retried after first attempt to retrieve fails`() = runTest {
        //Arrange
        val macAddress = "C8:1C:FE:1C:12:00"

        val emdkIntegration = mockk<EMDKIntegration>(relaxed = true) {
            coEvery {
                configure()
            } returns Unit.toKEMDKSuccess()
        }
        val oemInfoRequester = mockk<OEMInfoRequester>(relaxed = true) {
            coEvery {
                retrieveOEMInfo(any())
            } returns OEMInfoThrowable.DataNotAccessible.toKEMDKFailure() andThen macAddress.toKEMDKSuccess()
        }

        val sut = DeviceInformation(
            emdkIntegration = emdkIntegration,
            oemInfoRequester = oemInfoRequester,
            retryConfiguration = RetryConfiguration()
        )

        //Act
        val resultList = mutableListOf<EMDKResult<String, OEMInfoThrowable>>()

        launch {
            sut.retrieveOEMInfo(OEMInfo.BT_MAC).take(2).toList(resultList)
        }

        advanceUntilIdle()

        //Assert
        Assert.assertTrue(resultList.first() is EMDKResult.Failure)

        val res = resultList[1] as EMDKResult.Success
        Assert.assertEquals(macAddress, res.value)
    }

}