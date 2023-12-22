"use client";

import {ReactNode} from "react";
import {AppProgressBar as ProgressBar} from "next-nprogress-bar";
const NprogressProvider = ({children}) => {
    return (
        <>
            {children}
            <ProgressBar height="2px" color="#4F46E5" options={{showSpinner: false}} />
        </>
    );
};
export default NprogressProvider;
