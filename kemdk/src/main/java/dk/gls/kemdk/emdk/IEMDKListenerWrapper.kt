package dk.gls.kemdk.emdk

interface IEMDKListenerWrapper {

    fun onCompleted()

    fun onError(throwable: EMDKThrowable)

}