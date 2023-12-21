"use client"
import {
    Avatar, Dropdown,
    DropdownItem,
    DropdownMenu,
    DropdownTrigger,
    Navbar,
    NavbarBrand,
    NavbarContent,
    NavbarItem
} from "@nextui-org/react";
import Link from "next/link";
import Image from "next/image";
import useSWR from "swr";
import {useEffect} from "react";
import NavigationBar from "@/components/navbar";

export default function Home() {
    return (
        <main className="">
            <NavigationBar/>
        </main>
    )
}
