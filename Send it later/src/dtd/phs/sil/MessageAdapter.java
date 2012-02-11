package dtd.phs.sil;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import dtd.phs.sil.entities.IMessagesList;
import dtd.phs.sil.entities.MessageItem;
import dtd.phs.sil.utils.Helpers;

abstract public class MessageAdapter extends BaseAdapter {
	protected IMessagesList messages;
	protected Context context;

	protected Animation occAnim;
	protected Animation disAnim;
	protected ArrayList<Boolean> displayingDeleteButton;

	abstract public void onItemClick(View view, int position);
	abstract public void onItemLongClick(int position);

	protected final class OnDeleteButtonClickListener implements
	OnClickListener {
		private final MessageItem message;

		protected OnDeleteButtonClickListener(MessageItem message) {
			this.message = message;
		}

		@Override
		public void onClick(final View v) {
			long id = message.getId();
			modifyDatabaseOnDeleteButtonClick(id);
			int index = messages.removeMessageWithId(id);
			if ( index != -1 ) {
				displayingDeleteButton.remove(index);
				v.post(new Runnable() {
					@Override
					public void run() {
						v.setVisibility(View.GONE);
					}
				});
				Toast.makeText(context, R.string.message_removed_success, Toast.LENGTH_SHORT).show();
				notifyDataSetChanged();
			} else {
				Toast.makeText(context, R.string.message_removed_failed, Toast.LENGTH_SHORT).show();
			}
		}


	}

	public MessageAdapter(
			Context context,
			IMessagesList messagesList) {
		this.context = context;
		this.messages = messagesList;
		initDisplayingDeleteButton();
		this.occAnim = AnimationUtils.loadAnimation(context, R.anim.push_left_in);
		this.disAnim = AnimationUtils.loadAnimation(context,R.anim.push_right_out);
	}

	protected abstract void modifyDatabaseOnDeleteButtonClick(long id);

	protected void initDisplayingDeleteButton() {
		displayingDeleteButton = new ArrayList<Boolean>();
		for(int i = 0 ; i < messages.size() ; i++) displayingDeleteButton.add(new Boolean(false));
	}


	protected void makeButtonDispear(View delete,int position) {
		displayingDeleteButton.set(position, false);
		delete.startAnimation(disAnim);
		delete.setVisibility(View.GONE);
	}

	protected void updateDeleteButton(Button delete, int position, MessageItem message) {
		if ( displayingDeleteButton.get(position) ) {
			delete.setVisibility(View.VISIBLE);
		} else delete.setVisibility(View.GONE);
		delete.setOnClickListener(new OnDeleteButtonClickListener(message));
	}


	@Override
	public int getCount() {
		return messages.size();
	}

	@Override
	public Object getItem(int position) {
		return messages.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void setMessages(IMessagesList list) {
		this.messages = list;
		initDisplayingDeleteButton();
	}

	public MessageItem getMessage(int position) {
		return messages.get(position);

	}

	public long getMessageRowId(int position) {
		return messages.get(position).getId();
	}
	
	public class ViewHolder {
		public ImageView avatar;
		public TextView contact;
		public TextView content;
		public TextView status;
		public Button delete;
		public ImageView failedIcon;

	}
	
	public View getView(int position, View convertView, int itemLayout) {
		View v = convertView;
		ViewHolder holder;
		if ( v == null ) {
			v = Helpers.inflate(context, itemLayout);
			holder = new ViewHolder();
			createHolder(v, holder);
			v.setTag(holder);
		} else {
			holder = (ViewHolder) v.getTag();
		}
		updateView( holder, messages.get(position), position );
		processOnItemTouch(position, v);
		return v;	
	}

	abstract protected void processOnItemTouch(int position, View v);
	abstract protected void updateView(ViewHolder holder, MessageItem message,int position);
	protected void createHolder(View v, ViewHolder holder) {
		holder.avatar = (ImageView) v.findViewById(R.id.ivAvatar);
		holder.contact = (TextView) v.findViewById(R.id.tvContact);
		holder.content = (TextView) v.findViewById(R.id.tvContent);
		holder.status = (TextView) v.findViewById(R.id.tvStatus);
		holder.delete = (Button) v.findViewById(R.id.btDelete);
	}

}