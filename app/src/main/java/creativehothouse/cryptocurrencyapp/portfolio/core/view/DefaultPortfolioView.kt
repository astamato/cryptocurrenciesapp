package creativehothouse.cryptocurrencyapp.portfolio.core.view

import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import creativehothouse.cryptocurrencyapp.R
import creativehothouse.cryptocurrencyapp.R.layout
import creativehothouse.cryptocurrencyapp.app.model.Coin
import creativehothouse.cryptocurrencyapp.prices.core.view.adapter.CoinsPricesAdapter

class DefaultPortfolioView(context: Context) : CoordinatorLayout(context), PortfolioView {
  private lateinit var adapter: CoinsPricesAdapter

  private var progressBar: ProgressBar

  init {
    LinearLayout.inflate(context, layout.fragment_portfolio, this)
    progressBar = findViewById(R.id.progressBar)
  }

  override fun hideLoading() {
    progressBar.visibility = View.GONE
  }

  override fun showLoading() {
    progressBar.visibility = View.VISIBLE
  }

  override fun drawPortfolio(coins: List<Coin>) {
    val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewPortfolio)
    recyclerView.layoutManager = LinearLayoutManager(context)
    recyclerView.itemAnimator = DefaultItemAnimator()
    adapter = CoinsPricesAdapter(coins)
    recyclerView.adapter = adapter
  }

  override fun showErrorLoadingCoinList() {
    val container = getView().findViewById(R.id.placeSnackBar) as ViewGroup
    Snackbar.make(container
        , R.string.onErrorLoad, Snackbar.LENGTH_LONG)
        .show()
  }

  override fun getView(): View {
    return this
  }
}