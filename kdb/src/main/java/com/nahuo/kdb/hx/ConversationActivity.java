package com.nahuo.kdb.hx;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMConversationListener;
import com.hyphenate.EMError;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.PreferenceManager;
import com.hyphenate.easeui.widget.EaseConversationList;
import com.nahuo.kdb.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * 会话
 *
 * @author James Chen
 * @create time in 2017/6/5 13:23
 */
public class ConversationActivity extends Activity {
    protected InputMethodManager inputMethodManager;
    private final static int MSG_REFRESH = 2;
    protected EditText query;
    protected ImageButton clearSearch;
    protected List<EMConversation> conversationList = new ArrayList<EMConversation>();
    protected EaseConversationList conversationListView;
    protected FrameLayout errorItemContainer;

    protected boolean isConflict;

    protected EMConversationListener convListener = new EMConversationListener() {

        @Override
        public void onCoversationUpdate() {
            refresh();
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        PreferenceManager.init(this);
        initView();
        setUpView();
    }

    EMMessageListener messageListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            // notify new message
//            for (EMMessage message : messages) {
//               // DemoHelper.getInstance().getNotifier().onNewMsg(message);
//            }
            refresh();
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            //red packet code : 处理红包回执透传消息
//            for (EMMessage message : messages) {
//                EMCmdMessageBody cmdMsgBody = (EMCmdMessageBody) message.getBody();
//                final String action = cmdMsgBody.action();//获取自定义action
//                if (action.equals(RPConstant.REFRESH_GROUP_RED_PACKET_ACTION)) {
//                    RedPacketUtil.receiveRedPacketAckMessage(message);
//                }
//            }
//            //end of red packet code
//            refreshUIWithMessage();
        }

        @Override
        public void onMessageRead(List<EMMessage> messages) {
        }

        @Override
        public void onMessageDelivered(List<EMMessage> message) {
        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {
        }
    };


    private void initView() {
        findViewById(R.id.titlebar_btnLeft).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((TextView) findViewById(R.id.tv_title)).setText("消息");
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        conversationListView = (EaseConversationList) findViewById(R.id.list);
        query = (EditText) findViewById(R.id.query);
        // button to clear content in search bar
        clearSearch = (ImageButton) findViewById(R.id.search_clear);
        errorItemContainer = (FrameLayout) findViewById(R.id.fl_error_item);
    }


    protected void setUpView() {
        conversationList.addAll(loadConversationList());
        conversationListView.init(conversationList);
        registerForContextMenu(conversationListView);
//        if(listItemClickListener != null){
//
//        }
        conversationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EMConversation conversation = conversationListView.getItem(position);
                List<EMMessage> messages = conversation.getAllMessages();
                String userid = conversation.conversationId();
                String mename = EMClient.getInstance().getCurrentUser();
                String nick = PreferenceManager.getInstance().getToNickName(userid);
                startActivity(new Intent(ConversationActivity.this, ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, conversation.conversationId()).putExtra(EaseConstant.EXTRA_USER_NICK, nick));
                //listItemClickListener.onListItemClicked(conversation);
            }
        });
        EMClient.getInstance().addConnectionListener(connectionListener);

        query.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                conversationListView.filter(s);
                if (s.length() > 0) {
                    clearSearch.setVisibility(View.VISIBLE);
                } else {
                    clearSearch.setVisibility(View.INVISIBLE);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
        clearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query.getText().clear();
                hideSoftKeyboard();
            }
        });

        conversationListView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftKeyboard();
                return false;
            }
        });
    }


    protected EMConnectionListener connectionListener = new EMConnectionListener() {

        @Override
        public void onDisconnected(int error) {
            if (error == EMError.USER_REMOVED || error == EMError.USER_LOGIN_ANOTHER_DEVICE || error == EMError.SERVER_SERVICE_RESTRICTED) {
                isConflict = true;
            } else {
                handler.sendEmptyMessage(0);
            }
        }

        @Override
        public void onConnected() {
            handler.sendEmptyMessage(1);
        }
    };
    private EaseConversationListItemClickListener listItemClickListener;

    protected Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    onConnectionDisconnected();
                    break;
                case 1:
                    onConnectionConnected();
                    break;

                case MSG_REFRESH: {
                    conversationList.clear();
                    conversationList.addAll(loadConversationList());
                    conversationListView.refresh();
                    break;
                }
                default:
                    break;
            }
        }
    };

    /**
     * connected to server
     */
    protected void onConnectionConnected() {
        errorItemContainer.setVisibility(View.GONE);
    }

    /**
     * disconnected with server
     */
    protected void onConnectionDisconnected() {
        errorItemContainer.setVisibility(View.VISIBLE);
    }


    /**
     * refresh ui
     */
    public void refresh() {
        if (!handler.hasMessages(MSG_REFRESH)) {
            handler.sendEmptyMessage(MSG_REFRESH);
        }
    }

    /**
     * load conversation list
     *
     * @return +
     */
    protected List<EMConversation> loadConversationList() {
        // get all conversations
        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
        List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
        /**
         * lastMsgTime will change if there is new message during sorting
         * so use synchronized to make sure timestamp of last message won't change.
         */
        synchronized (conversations) {
            for (EMConversation conversation : conversations.values()) {
                if (conversation.getAllMessages().size() != 0) {
                    sortList.add(new Pair<Long, EMConversation>(conversation.getLastMessage().getMsgTime(), conversation));
                }
            }
        }
        try {
            // Internal is TimSort algorithm, has bug
            sortConversationByLastChatTime(sortList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<EMConversation> list = new ArrayList<EMConversation>();
        for (Pair<Long, EMConversation> sortItem : sortList) {
            list.add(sortItem.second);
        }
        return list;
    }

    /**
     * sort conversations according time stamp of last message
     *
     * @param conversationList
     */
    private void sortConversationByLastChatTime(List<Pair<Long, EMConversation>> conversationList) {
        Collections.sort(conversationList, new Comparator<Pair<Long, EMConversation>>() {
            @Override
            public int compare(final Pair<Long, EMConversation> con1, final Pair<Long, EMConversation> con2) {

                if (con1.first.equals(con2.first)) {
                    return 0;
                } else if (con2.first.longValue() > con1.first.longValue()) {
                    return 1;
                } else {
                    return -1;
                }
            }

        });
    }

    protected void hideSoftKeyboard() {
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        EMClient.getInstance().chatManager().addMessageListener(messageListener);
        refresh();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EMClient.getInstance().chatManager().removeMessageListener(messageListener);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EMClient.getInstance().removeConnectionListener(connectionListener);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (isConflict) {
            outState.putBoolean("isConflict", true);
        }
    }

    public interface EaseConversationListItemClickListener {
        /**
         * click event for conversation list
         *
         * @param conversation -- clicked item
         */
        void onListItemClicked(EMConversation conversation);
    }

    /**
     * set conversation list item click listener
     *
     * @param listItemClickListener
     */
    public void setConversationListItemClickListener(EaseConversationListItemClickListener listItemClickListener) {
        this.listItemClickListener = listItemClickListener;
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        getMenuInflater().inflate(R.menu.delete_message, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        boolean deleteMessage = false;
        if (item.getItemId() == R.id.delete_message) {
            deleteMessage = true;
        }
      /*  else if (item.getItemId() == R.id.delete_conversation) {
            deleteMessage = false;
        }*/
        EMConversation tobeDeleteCons = conversationListView.getItem(((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position);
        if (tobeDeleteCons == null) {
            return true;
        }
       /* if(tobeDeleteCons.getType() == EMConversationType.GroupChat){
            EaseAtMessageHelper.get().removeAtMeGroup(tobeDeleteCons.conversationId());
        }*/
        try {
            // delete conversation
            EMClient.getInstance().chatManager().deleteConversation(tobeDeleteCons.conversationId(), deleteMessage);
            /*InviteMessgeDao inviteMessgeDao = new InviteMessgeDao(getActivity());
            inviteMessgeDao.deleteMessage(tobeDeleteCons.conversationId());*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        refresh();

        // update unread count
      //  ((MainActivity) getActivity()).updateUnreadLabel();
        return true;
    }
}
