import {Popover, PopoverContent, PopoverTrigger} from "@nextui-org/react";
import {EmojiIcon} from "@/components/icons/EmojiIcon";
import EmojiPicker from "@/components/common/EmojiPicker";

export default function EmojiButton({inputEmoji}) {
    const onEmojiSelect = (emoji) => {
        if (!emoji) return;
        if (!inputEmoji) return;
        inputEmoji(emoji?.native);
    }

    return <Popover placement="bottom" showArrow={true} classNames={{
        content: "p-0"
    }}>
        <PopoverTrigger>
            <div
                className={"hover:bg-blue-100 rounded-full p-2 text-blue-700 cursor-pointer transition-all"}
            >
                <EmojiIcon/>
            </div>
        </PopoverTrigger>
        <PopoverContent>
            <EmojiPicker onEmojiSelect={onEmojiSelect}/>
        </PopoverContent>
    </Popover>
}
