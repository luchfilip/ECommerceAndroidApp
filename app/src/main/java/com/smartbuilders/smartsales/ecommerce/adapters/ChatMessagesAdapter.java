package com.smartbuilders.smartsales.ecommerce.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.smartbuilders.smartsales.ecommerce.BuildConfig;
import com.smartbuilders.smartsales.ecommerce.DialogAddToShoppingCart;
import com.smartbuilders.smartsales.ecommerce.DialogAddToShoppingSale;
import com.smartbuilders.smartsales.ecommerce.DialogUpdateShoppingCartQtyOrdered;
import com.smartbuilders.smartsales.ecommerce.ProductDetailActivity;
import com.smartbuilders.smartsales.ecommerce.R;
import com.smartbuilders.smartsales.ecommerce.data.ChatMessageDB;
import com.smartbuilders.smartsales.ecommerce.data.OrderLineDB;
import com.smartbuilders.smartsales.ecommerce.data.ProductDB;
import com.smartbuilders.smartsales.ecommerce.data.SalesOrderLineDB;
import com.smartbuilders.smartsales.ecommerce.model.ChatMessage;
import com.smartbuilders.smartsales.ecommerce.model.OrderLine;
import com.smartbuilders.smartsales.ecommerce.model.Product;
import com.smartbuilders.smartsales.ecommerce.model.SalesOrderLine;
import com.smartbuilders.smartsales.ecommerce.services.SendChatMessageService;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;
import com.smartbuilders.smartsales.salesforcesystem.DialogAddToShoppingSale2;
import com.smartbuilders.smartsales.salesforcesystem.DialogUpdateShoppingSaleQtyOrdered;
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.synchronizer.ids.model.UserProfile;

import java.util.ArrayList;

/**
 * Created by Alberto on 8/4/2016.
 */
public class ChatMessagesAdapter extends RecyclerView.Adapter<ChatMessagesAdapter.ViewHolder> {

    private FragmentActivity mFragmentActivity;
    private ArrayList<ChatMessage> mDataset;
    private Context mContext;
    private User mUser;
    private int mSenderChatContactId;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private View containerLayout;
        private TextView message;
        private TextView created;
        private TextView messageGroupCreateDate;
        private View sendProductPriceDividerLine;
        private View sendProductPriceTextView;
        private ImageView productImage;
        private TextView productName;
        private TextView productInternalCode;
        private View productLayout;
        public ImageView addToShoppingCartImageView;
        public ImageView addToShoppingSaleImageView;

        public ViewHolder(View v) {
            super(v);
            containerLayout = v.findViewById(R.id.container_layout);
            message = (TextView) v.findViewById(R.id.message);
            created = (TextView) v.findViewById(R.id.created);
            messageGroupCreateDate = (TextView) v.findViewById(R.id.message_group_created_date);
            sendProductPriceDividerLine = v.findViewById(R.id.send_product_price_divider);
            sendProductPriceTextView = v.findViewById(R.id.send_product_price_textView);
            productImage = (ImageView) v.findViewById(R.id.product_image);
            productName = (TextView) v.findViewById(R.id.product_name);
            productInternalCode = (TextView) v.findViewById(R.id.product_internal_code);
            addToShoppingCartImageView = (ImageView) v.findViewById(R.id.addToShoppingCart_imageView);
            addToShoppingSaleImageView = (ImageView) v.findViewById(R.id.addToShoppingSale_imageView);
            productLayout = v.findViewById(R.id.product_layout);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ChatMessagesAdapter(Context context, FragmentActivity fragmentActivity,
                               ArrayList<ChatMessage> myDataset, User user, int senderChatContactId) {
        mFragmentActivity = fragmentActivity;
        mDataset = myDataset;
        mUser = user;
        mContext = context;
        mSenderChatContactId = senderChatContactId;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ChatMessagesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_message_item, parent, false);
        // set the view's size, margins, paddings and layout parameters

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Product product = mDataset.get(position).getProductId()<=0 ? null :
                (new ProductDB(mContext, mUser)).getProductById(mDataset.get(holder.getAdapterPosition()).getProductId());

        if (position==0 || (position>0 && !mDataset.get(position).getCreatedDateStringFormat()
                .equals(mDataset.get(position-1).getCreatedDateStringFormat()))) {
            holder.messageGroupCreateDate.setText(mDataset.get(position).getCreatedDateStringFormat());
            holder.messageGroupCreateDate.setVisibility(View.VISIBLE);
        } else {
            holder.messageGroupCreateDate.setVisibility(View.GONE);
        }

        holder.message.setText(Html.fromHtml(mDataset.get(position).getMessage()));
        holder.created.setText(mDataset.get(position).getCreatedTimeStringFormat());
        if (mDataset.get(position).getSenderChatContactId() == mSenderChatContactId) {
            ((LinearLayout.LayoutParams) holder.containerLayout.getLayoutParams()).gravity = Gravity.RIGHT;
            ((LinearLayout.LayoutParams)holder.containerLayout.getLayoutParams()).setMargins(40, 0, 0, 0);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.containerLayout.setBackgroundResource(R.drawable.ripple_rounded_corners_chat_message_sent);
            } else {
                holder.containerLayout.setBackgroundResource(R.drawable.shape_selector_chat_message_sent);
            }
        } else {
            ((LinearLayout.LayoutParams) holder.containerLayout.getLayoutParams()).gravity = Gravity.LEFT;
            ((LinearLayout.LayoutParams)holder.containerLayout.getLayoutParams()).setMargins(0, 0, 40, 0);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.containerLayout.setBackgroundResource(R.drawable.ripple_rounded_corners_chat_message_received);
            } else {
                holder.containerLayout.setBackgroundResource(R.drawable.shape_selector_chat_message_received);
            }
        }
        holder.containerLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(mContext)
                        .setMessage(mContext.getString(R.string.delete_message))
                        .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String result = (new ChatMessageDB(mContext, mUser))
                                        .deactiveMessage(mSenderChatContactId, mDataset.get(holder.getAdapterPosition()).getId());
                                if (result==null) {
                                    mDataset.remove(holder.getAdapterPosition());
                                    //notifyItemRemoved(holder.getAdapterPosition());
                                    notifyDataSetChanged();
                                } else {
                                    Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .show();
                return true;
            }
        });
        if (mDataset.get(position).getProductId()>0) {
            holder.containerLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(product!=null){
                        mContext.startActivity((new Intent(mContext, ProductDetailActivity.class))
                                .putExtra(ProductDetailActivity.KEY_PRODUCT, product));
                    } else {
                        Toast.makeText(mContext, mContext.getString(R.string.no_product_details), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            holder.containerLayout.setOnClickListener(null);
        }

        if (mDataset.get(position).getSenderChatContactId()!=mSenderChatContactId
                && mDataset.get(position).getChatMessageType()==ChatMessage.TYPE_REQUEST_PRODUCT_PRICE
                && mDataset.get(position).getProductId()>0) {
            holder.sendProductPriceTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (product != null) {
                        Intent msgIntent = new Intent(mContext, SendChatMessageService.class);
                        msgIntent.putExtra(SendChatMessageService.KEY_USER_ID, mUser.getUserId());
                        ChatMessage chatMessage = new ChatMessage();
                        chatMessage.setSenderChatContactId(mSenderChatContactId);
                        chatMessage.setProductId(mDataset.get(holder.getAdapterPosition()).getProductId());
                        chatMessage.setChatMessageType(ChatMessage.TYPE_SEND_PRODUCT_PRICE);
                        chatMessage.setMessage(mContext.getString(R.string.send_product_price_chat_message,
                                product.getProductPriceAvailability().getPriceStringFormat()));
                        msgIntent.putExtra(SendChatMessageService.KEY_CHAT_MESSAGE, chatMessage);
                        mContext.startService(msgIntent);
                    } else {
                        Toast.makeText(mContext, mContext.getString(R.string.no_product_details), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            holder.sendProductPriceTextView.setVisibility(View.VISIBLE);
            holder.sendProductPriceDividerLine.setVisibility(View.VISIBLE);
        } else {
            holder.sendProductPriceTextView.setVisibility(View.GONE);
            holder.sendProductPriceDividerLine.setVisibility(View.GONE);
        }

        if (/*mDataset.get(position).getSenderChatContactId()!=mSenderChatContactId
                &&*/ mDataset.get(position).getChatMessageType()==ChatMessage.TYPE_SEND_PRODUCT_PRICE
                && product!=null) {
            Utils.loadOriginalImageByFileName(mContext, mUser, product.getImageFileName(), holder.productImage);
            holder.productName.setText(product.getName());
            holder.productInternalCode.setText(mContext.getString(R.string.product_internalCode, product.getInternalCodeMayoreoFormat()));

            if (holder.addToShoppingCartImageView!=null) {
                holder.addToShoppingCartImageView.setColorFilter(Utils.getColor(mContext, R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                holder.addToShoppingCartImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        OrderLine orderLine = (new OrderLineDB(mContext, mUser))
                                .getOrderLineFromShoppingCartByProductId(mDataset.get(holder.getAdapterPosition()).getId());
                        if (orderLine != null) {
                            updateQtyOrderedInShoppingCart(orderLine);
                        } else {
                            addToShoppingCart(product);
                        }
                    }
                });
            }

            if (holder.addToShoppingSaleImageView!=null) {
                holder.addToShoppingSaleImageView.setColorFilter(Utils.getColor(mContext, R.color.golden), PorterDuff.Mode.SRC_ATOP);
                holder.addToShoppingSaleImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (BuildConfig.IS_SALES_FORCE_SYSTEM
                                || (mUser.getUserProfileId() == UserProfile.SALES_MAN_PROFILE_ID && false)) {
                            try {
                                SalesOrderLine salesOrderLine = (new SalesOrderLineDB(mContext, mUser))
                                        .getSalesOrderLineFromShoppingSales(mDataset.get(holder.getAdapterPosition()).getId(),
                                                Utils.getAppCurrentBusinessPartnerId(mContext, mUser));
                                if (salesOrderLine != null) {
                                    updateQtyOrderedInShoppingSales(salesOrderLine);
                                } else {
                                    addToShoppingSale(product, false);
                                }
                            } catch (Exception e) {
                                //do nothing
                            }
                        } else {
                            addToShoppingSale(product, false);
                        }
                    }
                });
            }

            holder.productLayout.setVisibility(View.VISIBLE);
        } else {
            holder.productLayout.setVisibility(View.GONE);
        }
    }

    private void addToShoppingCart(Product product) {
        product = (new ProductDB(mContext, mUser)).getProductById(product.getId());
        if (product!=null) {
            DialogAddToShoppingCart dialogAddToShoppingCart =
                    DialogAddToShoppingCart.newInstance(product, mUser);
            dialogAddToShoppingCart.show(mFragmentActivity.getSupportFragmentManager(),
                    DialogAddToShoppingCart.class.getSimpleName());
        } else {
            //TODO: mostrar mensaje de error
        }
    }

    private void updateQtyOrderedInShoppingCart(OrderLine orderLine) {
        DialogUpdateShoppingCartQtyOrdered dialogUpdateShoppingCartQtyOrdered =
                DialogUpdateShoppingCartQtyOrdered.newInstance(orderLine, true, mUser);
        dialogUpdateShoppingCartQtyOrdered.show(mFragmentActivity.getSupportFragmentManager(),
                DialogUpdateShoppingCartQtyOrdered.class.getSimpleName());
    }

    private void addToShoppingSale(Product product, boolean managePriceInOrder) {
        product = (new ProductDB(mContext, mUser)).getProductById(product.getId());
        if (product!=null) {
            if (BuildConfig.IS_SALES_FORCE_SYSTEM
                    || (mUser.getUserProfileId() == UserProfile.SALES_MAN_PROFILE_ID && managePriceInOrder)) {
                DialogAddToShoppingSale2 dialogAddToShoppingSale2 =
                        DialogAddToShoppingSale2.newInstance(product, mUser);
                dialogAddToShoppingSale2.show(mFragmentActivity.getSupportFragmentManager(),
                        DialogAddToShoppingSale2.class.getSimpleName());
            } else {
                DialogAddToShoppingSale dialogAddToShoppingSale =
                        DialogAddToShoppingSale.newInstance(product, mUser);
                dialogAddToShoppingSale.show(mFragmentActivity.getSupportFragmentManager(),
                        DialogAddToShoppingSale.class.getSimpleName());
            }
        } else {
            //TODO: mostrar mensaje de error
        }
    }

    private void updateQtyOrderedInShoppingSales(SalesOrderLine salesOrderLine) {
        Product product = (new ProductDB(mContext, mUser)).getProductById(salesOrderLine.getProductId());
        if (product!=null) {
            DialogUpdateShoppingSaleQtyOrdered dialogUpdateShoppingSaleQtyOrdered =
                    DialogUpdateShoppingSaleQtyOrdered.newInstance(product, salesOrderLine, mUser);
            dialogUpdateShoppingSaleQtyOrdered.show(mFragmentActivity.getSupportFragmentManager(),
                    DialogUpdateShoppingSaleQtyOrdered.class.getSimpleName());
        } else {
            //TODO: mostrar mensaje de error
        }
    }

    public void setData(ArrayList<ChatMessage> data) {
        this.mDataset = data;
        notifyDataSetChanged();
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (mDataset!=null) {
            return mDataset.size();
        }
        return 0;
    }

    public void addChatMessage(ChatMessage chatMessage) {
        mDataset.add(chatMessage);
        notifyItemInserted(mDataset.indexOf(chatMessage));
    }
}