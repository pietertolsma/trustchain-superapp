package nl.tudelft.trustchain.eurotoken.ui.trustscores

import android.view.View
import com.mattskala.itemadapter.ItemLayoutRenderer
import kotlinx.android.synthetic.main.item_trustscore.view.*
import nl.tudelft.trustchain.eurotoken.R
import nl.tudelft.trustchain.eurotoken.entity.TrustScore

class TrustScoreItemRenderer(
    private val onItemLongClick: (TrustScore) -> Unit
) : ItemLayoutRenderer<TrustScoreItem, View>(
    TrustScoreItem::class.java
) {

    override fun bindView(item: TrustScoreItem, view: View) = with(view) {
        txtPubKey.text = item.trustScore.pubKey.toString()
        txtTrustScore.text = item.trustScore.trust.toString() + "%"
        setOnLongClickListener {
            onItemLongClick(item.trustScore)
            true
        }
    }

    override fun getLayoutResourceId(): Int {
        return R.layout.item_trustscore
    }
}
