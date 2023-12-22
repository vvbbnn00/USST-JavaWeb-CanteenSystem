import React, {useEffect, useRef} from "react";
import data from "@emoji-mart/data";
import {Picker} from "emoji-mart";

const EmojiPicker = (props) => {
    const ref = useRef(null);

    useEffect(() => {
        new Picker({
            ...props, data, ref
        });
    }, []);

    return <div ref={ref}/>;
};

export default EmojiPicker;
