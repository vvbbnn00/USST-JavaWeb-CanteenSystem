/*
BEGIN
    DECLARE new_level INT;

    -- 根据point值计算新等级
    SET new_level = CASE
        WHEN NEW.point < 5 THEN 1
        WHEN NEW.point < 15 THEN 2
        WHEN NEW.point < 30 THEN 3
        WHEN NEW.point < 50 THEN 4
        WHEN NEW.point < 100 THEN 5
        WHEN NEW.point < 200 THEN 6
        WHEN NEW.point < 500 THEN 7
        WHEN NEW.point < 1000 THEN 8
				WHEN NEW.point < 1000 THEN 8
        WHEN NEW.point < 3000 THEN 10
        WHEN NEW.point < 6000 THEN 11
        WHEN NEW.point < 10000 THEN 12
        WHEN NEW.point < 18000 THEN 13
        WHEN NEW.point < 30000 THEN 14
        WHEN NEW.point < 60000 THEN 15
        WHEN NEW.point < 100000 THEN 16
        WHEN NEW.point < 300000 THEN 17
        ELSE 18
    END;

    -- 更新用户等级
    SET NEW.level = new_level;
END
 */

const LEVELS = [
    0, 5, 15, 30, 50, 100, 200, 500, 1000, 3000, 6000, 10000, 18000, 30000, 60000, 100000, 300000
];

export function calcLevelAndProgress(exp) {
    let level = 0;
    let progress = 0;
    let nextLevelExp = 0;
    for (let i = 0; i < LEVELS.length; i++) {
        if (exp >= LEVELS[i]) {
            level = i;
            progress = i === LEVELS.length - 1 ? 1 : (exp - LEVELS[i]) / (LEVELS[i + 1] - LEVELS[i]);
            nextLevelExp = i === LEVELS.length - 1 ? 300000 : LEVELS[i + 1];
        } else {
            break;
        }
    }
    return {
        level,
        progress,
        nextLevelExp
    }
}


export function calcLevelColor(level) {
    switch (level) {
        case 0:
        case 1:
        case 2:
            return "gray";
        case 3:
        case 4:
        case 5:
            return "green";
        case 6:
        case 7:
        case 8:
        case 9:
        case 10:
            return "blue";
        case 11:
        case 12:
        case 13:
        case 14:
            return "orange";
        default:
            return "red";
    }
}
