package com.divyanshu.foodrunner.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.divyanshu.foodrunner.R
import com.divyanshu.foodrunner.activity.DescriptionActivity
import com.divyanshu.foodrunner.database.RestaurantEntity
import com.divyanshu.foodrunner.fragment.FavoritesFragment
import com.divyanshu.foodrunner.fragment.HomeFragment
import com.google.android.material.card.MaterialCardView
import com.squareup.picasso.Picasso

class FavoriteRecyclerViewAdapter(
    val context: Context,
    private var itemList: List<RestaurantEntity>
) :
    RecyclerView.Adapter<FavoriteRecyclerViewAdapter.FavoriteViewHolder>() {
    class FavoriteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val restaurantName: TextView = view.findViewById(R.id.txtRestaurantName)
        val restaurantImage: ImageView = view.findViewById(R.id.imgFoodImage)
        val pricePerPerson: TextView = view.findViewById(R.id.txtPrice)
        val restaurantRating: TextView = view.findViewById(R.id.txtRestaurantRating)
        val favoriteRestaurant: ImageView = view.findViewById(R.id.imgFavoriteImage)
        val cardView: MaterialCardView = view.findViewById(R.id.cardView)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavoriteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_home_row, parent, false)
        return FavoriteViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(
        holder: FavoriteViewHolder,
        position: Int
    ) {
        val restaurant = itemList[position]
        holder.restaurantName.text = restaurant.restaurantName
        Picasso.get().load(restaurant.restaurantImageUrl).error(R.drawable.ic_error_black_24dp)
            .into(holder.restaurantImage);
        holder.pricePerPerson.text = "₹${restaurant.restaurantCostForOne}/person"
        holder.restaurantRating.text = restaurant.restaurantRating

        val checkFav = HomeFragment.DBAsyncTask(context as Activity, restaurant, 1).execute()
        val isFav = checkFav.get()
        if (isFav) {
            holder.favoriteRestaurant.setImageResource(R.drawable.ic_favorite_colored_24dp)
        } else {
            holder.favoriteRestaurant.setImageResource(R.drawable.ic_favorite_outlined_colored_24dp)
        }
        holder.favoriteRestaurant.setOnClickListener {
            if (!HomeFragment.DBAsyncTask(context, restaurant, 1).execute().get()) {
                val async = HomeFragment.DBAsyncTask(context, restaurant, 2).execute()
                val result = async.get()
                if (result) {
                    Toast.makeText(
                        context,
                        "Restaurant Added To Favorites",
                        Toast.LENGTH_SHORT
                    ).show()
                    notifyDataSetChanged()
                } else {
                    Toast.makeText(
                        context,
                        "Some Error Occurred",
                        Toast.LENGTH_SHORT
                    ).show()
                    notifyDataSetChanged()
                }
            } else {
                val async = HomeFragment.DBAsyncTask(context, restaurant, 3).execute()
                val result = async.get()
                if (result) {
                    Toast.makeText(
                        context,
                        "Restaurant Removed From Favorites",
                        Toast.LENGTH_SHORT
                    ).show()
                    val dbRestaurantList =
                        FavoritesFragment.RetrieveFavorites(context).execute().get()
                    itemList = dbRestaurantList
                    notifyDataSetChanged()
                } else {
                    Toast.makeText(
                        context,
                        "Some Error Occurred",
                        Toast.LENGTH_SHORT
                    ).show()
                    notifyDataSetChanged()
                }
            }
        }
        holder.cardView.setOnClickListener {
            val intentToDescription = Intent(context, DescriptionActivity::class.java)
            intentToDescription.putExtra("restaurant_id", restaurant.restaurantId.toInt())
            intentToDescription.putExtra("restaurant_name", restaurant.restaurantName)
            intentToDescription.putExtra("restaurant_rating", restaurant.restaurantRating)
            intentToDescription.putExtra("restaurant_cost_for_one", restaurant.restaurantCostForOne)
            intentToDescription.putExtra("restaurant_image_url", restaurant.restaurantImageUrl)
            context.startActivity(intentToDescription)
        }
    }
}