"use client"

import {Button} from "@nextui-org/react";
import Image from "next/image";
import {useEffect, useState} from "react";
import RegisterForm from "@/app/auth/login/registerForm";
import LoginForm from "@/app/auth/login/loginForm";

export default function Login() {
    const textList = [
        {
            title: "美食探索，指尖上的味觉之旅",
            content: "加入我们的食堂点评社区，发现隐藏在每一角落的美味。我们提供最新、最全面的食堂排名，帮助您轻松找到心仪的美食。无论是传统小吃，还是创意料理，这里都有您想要的答案。"
        },
        {
            title: "发现味蕾新天地",
            content: "在我们的食堂点评社区，每一口都是一次新发现。从街角隐秘的美食到校园内的热门小吃，我们提供最新、最全的食堂排名。让您的每一餐，都是一次味蕾的探险旅行。"
        },
        {
            title: "美食之旅，触手可及",
            content: "加入我们，将世界各地的美味汇聚于您的指尖。我们的食堂点评社区为您提供最全面的食堂排名，无论是地道小吃还是国际美食，一应俱全。让您轻松品尝，尽享美食之旅。"
        },
        {
            title: "味蕾盛宴，尽在指尖",
            content: "我们的社区汇集了众多美食爱好者的真实点评，提供最新、最全的食堂排名。在这里，每一次点击都是对美味的探索，每一次选择都能满足您对美食的期待。"
        }
    ];

    const [currentText, setCurrentText] = useState(textList[0]);
    const [opacity, setOpacity] = useState(1);
    const year = new Date().getFullYear();

    const [form, setForm] = useState("");

    useEffect(() => {
        const interval = setInterval(() => {
            setOpacity(0);
            setTimeout(() => {
                setCurrentText(textList[Math.floor(Math.random() * textList.length)]);
                setOpacity(1);
            }, 500);
        }, 5000);
        return () => clearInterval(interval);
    });

    return (<>
            <main
                className="flex min-h-full w-screen flex-col sm:supports-[min-height:100dvh]:min-h-[100dvh] md:grid md:grid-cols-2 lg:grid-cols-[60%_40%]">
                <div
                    className={"relative hidden flex-1 flex-col justify-center px-5 pt-8 md:flex md:px-6 md:py-[22px] lg:px-8"}>
                    <header className={"flex items-center mb-8 absolute top-10"}>
                        <Image src={"/logo.png"} alt={"食堂点评交流社区"} width={48} height={48}
                               className={"rounded-xl mr-2.5"}/>
                        <h1 className="font-bold text-inherit text-xl">食堂点评交流社区</h1>
                    </header>
                    <div>
                        <div
                            className={"absolute left-0 top-[45%] flex w-full flex-col px-5 transition-[transform,opacity] duration-500 md:pl-6 md:pr-8 lg:pl-8 lg:pr-10 opacity-" + opacity}>
                            <p className={"font-bold text-3xl mb-2"}>{currentText.title}</p>
                            <p className={"text-gray-500 text-xl"}>{currentText.content}</p>
                        </div>
                    </div>
                </div>
                <div
                    className={"relative flex grow flex-col items-center justify-between bg-white px-5 py-8 text-black md:px-6"}>
                    <div
                        className={"absolute top-[50%] w-full p-5 2xl:pl-24 2xl:pr-24 transform transition-all opacity-" + (form === '' ? '1 -translate-y-1/2 ' : '0 pointer-events-none -translate-y-[40%]')}>
                        <p className={"font-bold text-center text-2xl mb-5"}>欢迎回来</p>
                        <div className={"flex w-full justify-between gap-3.5"}>
                            <Button color={"primary"} className={"w-full mb-3"} onClick={() => {
                                setForm("login")
                            }}>登录</Button>
                            <Button color={"primary"} className={"w-full"}>注册</Button>
                        </div>
                    </div>
                    <div
                        className={"absolute top-[50%] w-full p-5 2xl:pl-24 2xl:pr-24 transform -translate-y-1/2 transition-all delay-300 opacity-" + (form === 'login' ? '1 ' : '0 pointer-events-none select-none -translate-y-[45%]')}>
                        <LoginForm setForm={setForm}/>
                    </div>
                    <div
                        className={"absolute top-[50%] w-full p-5 2xl:pl-24 2xl:pr-24 transform -translate-y-1/2 transition-all delay-300 opacity-" + (form === 'register' ? '1 ' : '0 pointer-events-none select-none -translate-y-[45%]')}>
                        <RegisterForm setForm={setForm}/>
                    </div>

                    <footer className={"absolute bottom-10"}>
                        <p className={"text-gray-500 text-sm text-center"}>© {year} 上海理工大学食堂点评交流社区</p>
                    </footer>
                </div>
            </main>
        </>
    )
}
