package jp.co.internous.team2404.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

import jp.co.internous.team2404.model.domain.MstDestination;
import jp.co.internous.team2404.model.mapper.MstDestinationMapper;
import jp.co.internous.team2404.model.mapper.TblCartMapper;
import jp.co.internous.team2404.model.mapper.TblPurchaseHistoryMapper;
import jp.co.internous.team2404.model.session.LoginSession;

/**
 * 決済に関する処理を行うコントローラー
 * @author インターノウス
 *
 */
@Controller
@RequestMapping("/team2404/settlement")
public class SettlementController {
	/*
	 * フィールド定義
	 */
	@Autowired
	private MstDestinationMapper destinationMapper;

	@Autowired
	private TblPurchaseHistoryMapper purchaseHistoryMapper;

	@Autowired
	private TblCartMapper cartMapper;

	@Autowired
	private LoginSession loginSession;

	private Gson gson = new Gson();

	/**
	 * 宛先選択・決済画面を初期表示する。
	 * @param m 画面表示用オブジェクト
	 * @return 宛先選択・決済画面
	 */
	@RequestMapping("/")
	public String index(Model m) {
		int userId = loginSession.getUserId();
		List<MstDestination> destination = destinationMapper.findByUserId(userId);
		m.addAttribute("destinations", destination);
		m.addAttribute("loginSession", loginSession);

		return "settlement";
	}

	/**
	 * 決済ボタンを押された場合は、決済処理を行う
	 * 決済処理を行う
	 * @param destinationId 宛先情報id
	 * @return true:決済処理成功、false:決済処理失敗
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/complete")
	@ResponseBody
	public boolean complete(@RequestBody String destinationId) {
		Map<String, String> destinationIdMap = gson.fromJson(destinationId, Map.class);
		int id = Integer.parseInt(destinationIdMap.get("destinationId"));
		int userId = loginSession.getUserId();
		int destination = purchaseHistoryMapper.insert(id, userId);
		int cartCount = 0;
		if (destination > 0) {
			cartCount = cartMapper.deleteByUserId(userId);
		}
		return cartCount == destination;
	}
}
