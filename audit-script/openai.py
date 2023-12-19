import logging

import requests

from config import OPENAI_BASE_URL, OPENAI_KEY


def get_text_moderation(input_text: str) -> dict:
    """
    文本审核
    :param input_text: 文本
    :return: {
        "success": True,
        ["error": "",],
        "flagged": True,
        "categories": {...},
        "category_scores": {...},
    }
    """
    url = f"{OPENAI_BASE_URL}/v1/moderations"
    payload = {
        "input": input_text,
    }
    headers = {
        "Content-Type": "application/json",
        "Authorization": f"Bearer {OPENAI_KEY}"
    }
    result = None
    try:
        response = requests.post(url, json=payload, headers=headers)
        result = response.json()
        # logger.info(f"OpenAI返回结果：{result}")
        if result.get("error") is not None:
            return {
                "success": False,
                "message": "OpenAI: " + result['error']['message']
            }
        result = result["results"][0]
    except Exception as e:
        logging.exception("请求OpenAI出错")
        if result:
            logging.info(f"OpenAI返回结果：{result}")
        return {
            "success": False,
            "error": "请求OpenAI出错，请查看日志"
        }

    return {
        "success": True,
        **result
    }


if __name__ == '__main__':
    print(get_text_moderation("你瞧瞧你那个b样，人不人鬼不鬼的，还配吃美食？吔屎啦你"))
