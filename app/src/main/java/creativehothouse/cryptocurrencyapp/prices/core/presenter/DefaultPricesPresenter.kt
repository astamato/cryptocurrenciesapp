package creativehothouse.cryptocurrencyapp.prices.core.presenter

import android.util.Log
import android.view.View
import android.widget.Toast
import creativehothouse.cryptocurrencyapp.app.model.PricesListResponseModel
import creativehothouse.cryptocurrencyapp.prices.core.interactor.PricesInteractor
import creativehothouse.cryptocurrencyapp.prices.core.view.PricesView
import creativehothouse.cryptocurrencyapp.prices.wireframe.PricesWireframe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class DefaultPricesPresenter(val view: PricesView,
                             val interactor: PricesInteractor,
                             val wireframe: PricesWireframe) : PricesPresenter {


    private var disposables = CompositeDisposable()

    override fun create() {
        disposables.add(this.observeGetListOfCoins()!!)
    }

    private fun observeGetListOfCoins(): Disposable? {
        return interactor.getCryptoCurrenciesList()
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { view.showLoading() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result -> onGetCryptoCurrenciesListSuccess(result) },
                        { throwable -> onGetCryptoCurrenciesListFail(throwable) }
                )
    }

    private fun observeTicketIsSelected(): Disposable {
        return view.onCoinIsSelected()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { coin -> wireframe.onCoinSelected(coin) },
                        { throwable -> onGetCryptoCurrenciesListFail(throwable) }
                )
    }

    private fun observeOnLoadMore(): Disposable {
        return view.onLoadMore()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { _ ->
                            if (interactor.hasNextPage()) {
                                interactor.getCryptoCurrenciesListByPage(interactor.getNextPage())
                                        .subscribeOn(Schedulers.io())
                                        .doOnSubscribe { view.showLoading() }
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(
                                                { result -> onGetCryptoCurrenciesAddToListSuccess(result) },
                                                { throwable -> onGetCryptoCurrenciesListFail(throwable) }
                                        )
                            }
                        },
                        { throwable -> onGetCryptoCurrenciesListFail(throwable) }
                )
    }

    override fun getView(): View = view.getView()


    override fun onGetCryptoCurrenciesListSuccess(responseModel: PricesListResponseModel) {
        view.hideLoading()
        interactor.storeOrUpdatePagesModel(responseModel)
        view.drawCoinsList(responseModel)
        disposables.add(observeTicketIsSelected())
        disposables.add(observeOnLoadMore())
    }

    override fun onGetCryptoCurrenciesAddToListSuccess(responseModel: PricesListResponseModel) {
        view.hideLoading()
        view.setLoaded()
        interactor.storeOrUpdatePagesModel(responseModel)
        view.addToCoins(responseModel)
    }

    override fun onGetCryptoCurrenciesListFail(it: Throwable?) {
        view.hideLoading()
        view.setLoaded()
        view.showErrorLoadingCoinList()
        Log.e("DefaultPricesPresenter", "Error when trying to retrieve the cryptocurrencies", it)
    }

    override fun onCryptoCurrencyIsSelected(): Disposable {
        return view.onCoinIsSelected()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    Toast.makeText(view.getView().context, "Clicked on $it", Toast.LENGTH_SHORT).show()
                }
    }

    override fun destroy() {
        disposables.clear()
    }

}