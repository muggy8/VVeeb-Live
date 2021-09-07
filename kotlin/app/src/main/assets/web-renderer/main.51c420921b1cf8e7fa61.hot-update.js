self["webpackHotUpdate"]("main",{

/***/ "./src/lappmodel.ts":
/*!**************************!*\
  !*** ./src/lappmodel.ts ***!
  \**************************/
/***/ (function(__unused_webpack_module, exports, __webpack_require__) {

"use strict";

var __extends = (this && this.__extends) || (function () {
    var extendStatics = function (d, b) {
        extendStatics = Object.setPrototypeOf ||
            ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
            function (d, b) { for (var p in b) if (Object.prototype.hasOwnProperty.call(b, p)) d[p] = b[p]; };
        return extendStatics(d, b);
    };
    return function (d, b) {
        if (typeof b !== "function" && b !== null)
            throw new TypeError("Class extends value " + String(b) + " is not a constructor or null");
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
var __createBinding = (this && this.__createBinding) || (Object.create ? (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    Object.defineProperty(o, k2, { enumerable: true, get: function() { return m[k]; } });
}) : (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    o[k2] = m[k];
}));
var __setModuleDefault = (this && this.__setModuleDefault) || (Object.create ? (function(o, v) {
    Object.defineProperty(o, "default", { enumerable: true, value: v });
}) : function(o, v) {
    o["default"] = v;
});
var __importStar = (this && this.__importStar) || function (mod) {
    if (mod && mod.__esModule) return mod;
    var result = {};
    if (mod != null) for (var k in mod) if (k !== "default" && Object.prototype.hasOwnProperty.call(mod, k)) __createBinding(result, mod, k);
    __setModuleDefault(result, mod);
    return result;
};
Object.defineProperty(exports, "__esModule", ({ value: true }));
exports.LAppModel = void 0;
__webpack_require__(/*! whatwg-fetch */ "./node_modules/whatwg-fetch/fetch.js");
var cubismdefaultparameterid_1 = __webpack_require__(/*! @framework/cubismdefaultparameterid */ "../../../Framework/src/cubismdefaultparameterid.ts");
var cubismmodelsettingjson_1 = __webpack_require__(/*! @framework/cubismmodelsettingjson */ "../../../Framework/src/cubismmodelsettingjson.ts");
var cubismbreath_1 = __webpack_require__(/*! @framework/effect/cubismbreath */ "../../../Framework/src/effect/cubismbreath.ts");
var live2dcubismframework_1 = __webpack_require__(/*! @framework/live2dcubismframework */ "../../../Framework/src/live2dcubismframework.ts");
var cubismusermodel_1 = __webpack_require__(/*! @framework/model/cubismusermodel */ "../../../Framework/src/model/cubismusermodel.ts");
var acubismmotion_1 = __webpack_require__(/*! @framework/motion/acubismmotion */ "../../../Framework/src/motion/acubismmotion.ts");
var cubismmotionqueuemanager_1 = __webpack_require__(/*! @framework/motion/cubismmotionqueuemanager */ "../../../Framework/src/motion/cubismmotionqueuemanager.ts");
var csmmap_1 = __webpack_require__(/*! @framework/type/csmmap */ "../../../Framework/src/type/csmmap.ts");
var csmvector_1 = __webpack_require__(/*! @framework/type/csmvector */ "../../../Framework/src/type/csmvector.ts");
var cubismdebug_1 = __webpack_require__(/*! @framework/utils/cubismdebug */ "../../../Framework/src/utils/cubismdebug.ts");
var LAppDefine = __importStar(__webpack_require__(/*! ./lappdefine */ "./src/lappdefine.ts"));
var lappdelegate_1 = __webpack_require__(/*! ./lappdelegate */ "./src/lappdelegate.ts");
var lapppal_1 = __webpack_require__(/*! ./lapppal */ "./src/lapppal.ts");
var lappwavfilehandler_1 = __webpack_require__(/*! ./lappwavfilehandler */ "./src/lappwavfilehandler.ts");
var LoadStep;
(function (LoadStep) {
    LoadStep[LoadStep["LoadAssets"] = 0] = "LoadAssets";
    LoadStep[LoadStep["LoadModel"] = 1] = "LoadModel";
    LoadStep[LoadStep["WaitLoadModel"] = 2] = "WaitLoadModel";
    LoadStep[LoadStep["LoadExpression"] = 3] = "LoadExpression";
    LoadStep[LoadStep["WaitLoadExpression"] = 4] = "WaitLoadExpression";
    LoadStep[LoadStep["LoadPhysics"] = 5] = "LoadPhysics";
    LoadStep[LoadStep["WaitLoadPhysics"] = 6] = "WaitLoadPhysics";
    LoadStep[LoadStep["LoadPose"] = 7] = "LoadPose";
    LoadStep[LoadStep["WaitLoadPose"] = 8] = "WaitLoadPose";
    LoadStep[LoadStep["SetupEyeBlink"] = 9] = "SetupEyeBlink";
    LoadStep[LoadStep["SetupBreath"] = 10] = "SetupBreath";
    LoadStep[LoadStep["LoadUserData"] = 11] = "LoadUserData";
    LoadStep[LoadStep["WaitLoadUserData"] = 12] = "WaitLoadUserData";
    LoadStep[LoadStep["SetupEyeBlinkIds"] = 13] = "SetupEyeBlinkIds";
    LoadStep[LoadStep["SetupLipSyncIds"] = 14] = "SetupLipSyncIds";
    LoadStep[LoadStep["SetupLayout"] = 15] = "SetupLayout";
    LoadStep[LoadStep["LoadMotion"] = 16] = "LoadMotion";
    LoadStep[LoadStep["WaitLoadMotion"] = 17] = "WaitLoadMotion";
    LoadStep[LoadStep["CompleteInitialize"] = 18] = "CompleteInitialize";
    LoadStep[LoadStep["CompleteSetupModel"] = 19] = "CompleteSetupModel";
    LoadStep[LoadStep["LoadTexture"] = 20] = "LoadTexture";
    LoadStep[LoadStep["WaitLoadTexture"] = 21] = "WaitLoadTexture";
    LoadStep[LoadStep["CompleteSetup"] = 22] = "CompleteSetup";
})(LoadStep || (LoadStep = {}));
var LAppModel = (function (_super) {
    __extends(LAppModel, _super);
    function LAppModel() {
        var _this = _super.call(this) || this;
        _this._modelSetting = null;
        _this._modelHomeDir = null;
        _this._userTimeSeconds = 0.0;
        _this._eyeBlinkIds = new csmvector_1.csmVector();
        _this._lipSyncIds = new csmvector_1.csmVector();
        _this._motions = new csmmap_1.csmMap();
        _this._expressions = new csmmap_1.csmMap();
        _this._hitArea = new csmvector_1.csmVector();
        _this._userArea = new csmvector_1.csmVector();
        _this._idParamAngleX = live2dcubismframework_1.CubismFramework.getIdManager().getId(cubismdefaultparameterid_1.CubismDefaultParameterId.ParamAngleX);
        _this._idParamAngleY = live2dcubismframework_1.CubismFramework.getIdManager().getId(cubismdefaultparameterid_1.CubismDefaultParameterId.ParamAngleY);
        _this._idParamAngleZ = live2dcubismframework_1.CubismFramework.getIdManager().getId(cubismdefaultparameterid_1.CubismDefaultParameterId.ParamAngleZ);
        _this._idParamEyeBallX = live2dcubismframework_1.CubismFramework.getIdManager().getId(cubismdefaultparameterid_1.CubismDefaultParameterId.ParamEyeBallX);
        _this._idParamEyeBallY = live2dcubismframework_1.CubismFramework.getIdManager().getId(cubismdefaultparameterid_1.CubismDefaultParameterId.ParamEyeBallY);
        _this._idParamBodyAngleX = live2dcubismframework_1.CubismFramework.getIdManager().getId(cubismdefaultparameterid_1.CubismDefaultParameterId.ParamBodyAngleX);
        _this._state = LoadStep.LoadAssets;
        _this._expressionCount = 0;
        _this._textureCount = 0;
        _this._motionCount = 0;
        _this._allMotionCount = 0;
        _this._wavFileHandler = new lappwavfilehandler_1.LAppWavFileHandler();
        _this._appHostParams = {};
        if (window.appHost) {
            window.appHost.on("params", function (newAppHostParams) {
                Object.assign(_this._appHostParams, newAppHostParams);
            });
        }
        return _this;
    }
    LAppModel.prototype.loadAssets = function (dir, fileName) {
        var _this = this;
        this._modelHomeDir = dir;
        fetch("" + this._modelHomeDir + fileName)
            .then(function (response) { return response.arrayBuffer(); })
            .then(function (arrayBuffer) {
            var setting = new cubismmodelsettingjson_1.CubismModelSettingJson(arrayBuffer, arrayBuffer.byteLength);
            _this._state = LoadStep.LoadModel;
            _this.setupModel(setting);
        });
    };
    LAppModel.prototype.setupModel = function (setting) {
        var _this = this;
        this._updating = true;
        this._initialized = false;
        this._modelSetting = setting;
        if (this._modelSetting.getModelFileName() != '') {
            var modelFileName = this._modelSetting.getModelFileName();
            fetch("" + this._modelHomeDir + modelFileName)
                .then(function (response) { return response.arrayBuffer(); })
                .then(function (arrayBuffer) {
                _this.loadModel(arrayBuffer);
                _this._state = LoadStep.LoadExpression;
                loadCubismExpression();
            });
            this._state = LoadStep.WaitLoadModel;
        }
        else {
            lapppal_1.LAppPal.printMessage('Model data does not exist.');
        }
        var loadCubismExpression = function () {
            if (_this._modelSetting.getExpressionCount() > 0) {
                var count_1 = _this._modelSetting.getExpressionCount();
                var _loop_1 = function (i) {
                    var expressionName = _this._modelSetting.getExpressionName(i);
                    var expressionFileName = _this._modelSetting.getExpressionFileName(i);
                    fetch("" + _this._modelHomeDir + expressionFileName)
                        .then(function (response) { return response.arrayBuffer(); })
                        .then(function (arrayBuffer) {
                        var motion = _this.loadExpression(arrayBuffer, arrayBuffer.byteLength, expressionName);
                        if (_this._expressions.getValue(expressionName) != null) {
                            acubismmotion_1.ACubismMotion.delete(_this._expressions.getValue(expressionName));
                            _this._expressions.setValue(expressionName, null);
                        }
                        _this._expressions.setValue(expressionName, motion);
                        _this._expressionCount++;
                        if (_this._expressionCount >= count_1) {
                            _this._state = LoadStep.LoadPhysics;
                            loadCubismPhysics();
                        }
                    });
                };
                for (var i = 0; i < count_1; i++) {
                    _loop_1(i);
                }
                _this._state = LoadStep.WaitLoadExpression;
            }
            else {
                _this._state = LoadStep.LoadPhysics;
                loadCubismPhysics();
            }
        };
        var loadCubismPhysics = function () {
            if (_this._modelSetting.getPhysicsFileName() != '') {
                var physicsFileName = _this._modelSetting.getPhysicsFileName();
                fetch("" + _this._modelHomeDir + physicsFileName)
                    .then(function (response) { return response.arrayBuffer(); })
                    .then(function (arrayBuffer) {
                    _this.loadPhysics(arrayBuffer, arrayBuffer.byteLength);
                    _this._state = LoadStep.LoadPose;
                    loadCubismPose();
                });
                _this._state = LoadStep.WaitLoadPhysics;
            }
            else {
                _this._state = LoadStep.LoadPose;
                loadCubismPose();
            }
        };
        var loadCubismPose = function () {
            if (_this._modelSetting.getPoseFileName() != '') {
                var poseFileName = _this._modelSetting.getPoseFileName();
                fetch("" + _this._modelHomeDir + poseFileName)
                    .then(function (response) { return response.arrayBuffer(); })
                    .then(function (arrayBuffer) {
                    _this.loadPose(arrayBuffer, arrayBuffer.byteLength);
                    _this._state = LoadStep.SetupEyeBlink;
                    setupEyeBlink();
                });
                _this._state = LoadStep.WaitLoadPose;
            }
            else {
                _this._state = LoadStep.SetupEyeBlink;
                setupEyeBlink();
            }
        };
        var setupEyeBlink = function () {
            setupBreath();
        };
        var setupBreath = function () {
            _this._breath = cubismbreath_1.CubismBreath.create();
            var breathParameters = new csmvector_1.csmVector();
            breathParameters.pushBack(new cubismbreath_1.BreathParameterData(live2dcubismframework_1.CubismFramework.getIdManager().getId(cubismdefaultparameterid_1.CubismDefaultParameterId.ParamBreath), 0.0, 0.5, 3.2345, 0.5));
            _this._breath.setParameters(breathParameters);
            _this._state = LoadStep.LoadUserData;
            loadUserData();
        };
        var loadUserData = function () {
            if (_this._modelSetting.getUserDataFile() != '') {
                var userDataFile = _this._modelSetting.getUserDataFile();
                fetch("" + _this._modelHomeDir + userDataFile)
                    .then(function (response) { return response.arrayBuffer(); })
                    .then(function (arrayBuffer) {
                    _this.loadUserData(arrayBuffer, arrayBuffer.byteLength);
                    _this._state = LoadStep.SetupEyeBlinkIds;
                    setupEyeBlinkIds();
                });
                _this._state = LoadStep.WaitLoadUserData;
            }
            else {
                _this._state = LoadStep.SetupEyeBlinkIds;
                setupEyeBlinkIds();
            }
        };
        var setupEyeBlinkIds = function () {
            var eyeBlinkIdCount = _this._modelSetting.getEyeBlinkParameterCount();
            for (var i = 0; i < eyeBlinkIdCount; ++i) {
                _this._eyeBlinkIds.pushBack(_this._modelSetting.getEyeBlinkParameterId(i));
            }
            _this._state = LoadStep.SetupLipSyncIds;
            setupLipSyncIds();
        };
        var setupLipSyncIds = function () {
            var lipSyncIdCount = _this._modelSetting.getLipSyncParameterCount();
            for (var i = 0; i < lipSyncIdCount; ++i) {
                _this._lipSyncIds.pushBack(_this._modelSetting.getLipSyncParameterId(i));
            }
            _this._state = LoadStep.SetupLayout;
            setupLayout();
        };
        var setupLayout = function () {
            var layout = new csmmap_1.csmMap();
            _this._modelSetting.getLayoutMap(layout);
            _this._modelMatrix.setupFromLayout(layout);
            _this._state = LoadStep.LoadMotion;
            loadCubismMotion();
        };
        var loadCubismMotion = function () {
            _this._state = LoadStep.WaitLoadMotion;
            _this._model.saveParameters();
            _this._allMotionCount = 0;
            _this._motionCount = 0;
            var group = [];
            var motionGroupCount = _this._modelSetting.getMotionGroupCount();
            for (var i = 0; i < motionGroupCount; i++) {
                group[i] = _this._modelSetting.getMotionGroupName(i);
                _this._allMotionCount += _this._modelSetting.getMotionCount(group[i]);
            }
            for (var i = 0; i < motionGroupCount; i++) {
                _this.preLoadMotionGroup(group[i]);
            }
            if (motionGroupCount == 0) {
                _this._state = LoadStep.LoadTexture;
                _this._motionManager.stopAllMotions();
                _this._updating = false;
                _this._initialized = true;
                _this.createRenderer();
                _this.setupTextures();
                _this.getRenderer().startUp(lappdelegate_1.gl);
            }
        };
    };
    LAppModel.prototype.setupTextures = function () {
        var _this = this;
        var usePremultiply = true;
        if (this._state == LoadStep.LoadTexture) {
            var textureCount_1 = this._modelSetting.getTextureCount();
            var _loop_2 = function (modelTextureNumber) {
                if (this_1._modelSetting.getTextureFileName(modelTextureNumber) == '') {
                    console.log('getTextureFileName null');
                    return "continue";
                }
                var texturePath = this_1._modelSetting.getTextureFileName(modelTextureNumber);
                texturePath = this_1._modelHomeDir + texturePath;
                var onLoad = function (textureInfo) {
                    _this.getRenderer().bindTexture(modelTextureNumber, textureInfo.id);
                    _this._textureCount++;
                    if (_this._textureCount >= textureCount_1) {
                        _this._state = LoadStep.CompleteSetup;
                    }
                };
                lappdelegate_1.LAppDelegate.getInstance()
                    .getTextureManager()
                    .createTextureFromPngFile(texturePath, usePremultiply, onLoad);
                this_1.getRenderer().setIsPremultipliedAlpha(usePremultiply);
            };
            var this_1 = this;
            for (var modelTextureNumber = 0; modelTextureNumber < textureCount_1; modelTextureNumber++) {
                _loop_2(modelTextureNumber);
            }
            this._state = LoadStep.WaitLoadTexture;
        }
    };
    LAppModel.prototype.reloadRenderer = function () {
        this.deleteRenderer();
        this.createRenderer();
        this.setupTextures();
    };
    LAppModel.prototype.update = function () {
        if (this._state != LoadStep.CompleteSetup)
            return;
        var deltaTimeSeconds = lapppal_1.LAppPal.getDeltaTime();
        this._userTimeSeconds += deltaTimeSeconds;
        this._dragManager.update(deltaTimeSeconds);
        this._dragX = this._dragManager.getX();
        this._dragY = this._dragManager.getY();
        var motionUpdated = false;
        this._model.loadParameters();
        if (!motionUpdated) {
            if (this._eyeBlink != null) {
                this._eyeBlink.updateParameters(this._model, deltaTimeSeconds);
            }
        }
        if (this._expressionManager != null) {
            this._expressionManager.updateMotion(this._model, deltaTimeSeconds);
        }
        var hostParams = Object.keys(this._appHostParams);
        for (var i = 0, param = void 0; param = hostParams[i]; i++, param.startsWith("Param") && this._model.addParameterValueById(live2dcubismframework_1.CubismFramework.getIdManager().getId(param), this._appHostParams[param]))
            ;
        if (this._breath != null) {
            this._breath.updateParameters(this._model, deltaTimeSeconds);
        }
        if (this._physics != null) {
            this._physics.evaluate(this._model, deltaTimeSeconds);
        }
        if (this._lipsync) {
            var value = 0.0;
            this._wavFileHandler.update(deltaTimeSeconds);
            value = this._wavFileHandler.getRms();
            for (var i = 0; i < this._lipSyncIds.getSize(); ++i) {
                this._model.addParameterValueById(this._lipSyncIds.at(i), value, 0.8);
            }
        }
        if (this._pose != null) {
            this._pose.updateParameters(this._model, deltaTimeSeconds);
        }
        this._model.update();
    };
    LAppModel.prototype.startMotion = function (group, no, priority, onFinishedMotionHandler) {
        var _this = this;
        if (priority == LAppDefine.PriorityForce) {
            this._motionManager.setReservePriority(priority);
        }
        else if (!this._motionManager.reserveMotion(priority)) {
            if (this._debugMode) {
                lapppal_1.LAppPal.printMessage("[APP]can't start motion.");
            }
            return cubismmotionqueuemanager_1.InvalidMotionQueueEntryHandleValue;
        }
        var motionFileName = this._modelSetting.getMotionFileName(group, no);
        var name = group + "_" + no;
        var motion = this._motions.getValue(name);
        var autoDelete = false;
        if (motion == null) {
            fetch("" + this._modelHomeDir + motionFileName)
                .then(function (response) { return response.arrayBuffer(); })
                .then(function (arrayBuffer) {
                motion = _this.loadMotion(arrayBuffer, arrayBuffer.byteLength, null, onFinishedMotionHandler);
                var fadeTime = _this._modelSetting.getMotionFadeInTimeValue(group, no);
                if (fadeTime >= 0.0) {
                    motion.setFadeInTime(fadeTime);
                }
                fadeTime = _this._modelSetting.getMotionFadeOutTimeValue(group, no);
                if (fadeTime >= 0.0) {
                    motion.setFadeOutTime(fadeTime);
                }
                motion.setEffectIds(_this._eyeBlinkIds, _this._lipSyncIds);
                autoDelete = true;
            });
        }
        else {
            motion.setFinishedMotionHandler(onFinishedMotionHandler);
        }
        var voice = this._modelSetting.getMotionSoundFileName(group, no);
        if (voice.localeCompare('') != 0) {
            var path = voice;
            path = this._modelHomeDir + path;
            this._wavFileHandler.start(path);
        }
        if (this._debugMode) {
            lapppal_1.LAppPal.printMessage("[APP]start motion: [" + group + "_" + no);
        }
        return this._motionManager.startMotionPriority(motion, autoDelete, priority);
    };
    LAppModel.prototype.startRandomMotion = function (group, priority, onFinishedMotionHandler) {
        if (this._modelSetting.getMotionCount(group) == 0) {
            return cubismmotionqueuemanager_1.InvalidMotionQueueEntryHandleValue;
        }
        var no = Math.floor(Math.random() * this._modelSetting.getMotionCount(group));
        return this.startMotion(group, no, priority, onFinishedMotionHandler);
    };
    LAppModel.prototype.setExpression = function (expressionId) {
        var motion = this._expressions.getValue(expressionId);
        if (this._debugMode) {
            lapppal_1.LAppPal.printMessage("[APP]expression: [" + expressionId + "]");
        }
        if (motion != null) {
            this._expressionManager.startMotionPriority(motion, false, LAppDefine.PriorityForce);
        }
        else {
            if (this._debugMode) {
                lapppal_1.LAppPal.printMessage("[APP]expression[" + expressionId + "] is null");
            }
        }
    };
    LAppModel.prototype.setRandomExpression = function () {
        if (this._expressions.getSize() == 0) {
            return;
        }
        var no = Math.floor(Math.random() * this._expressions.getSize());
        for (var i = 0; i < this._expressions.getSize(); i++) {
            if (i == no) {
                var name_1 = this._expressions._keyValues[i].first;
                this.setExpression(name_1);
                return;
            }
        }
    };
    LAppModel.prototype.motionEventFired = function (eventValue) {
        cubismdebug_1.CubismLogInfo('{0} is fired on LAppModel!!', eventValue.s);
    };
    LAppModel.prototype.hitTest = function (hitArenaName, x, y) {
        if (this._opacity < 1) {
            return false;
        }
        var count = this._modelSetting.getHitAreasCount();
        for (var i = 0; i < count; i++) {
            if (this._modelSetting.getHitAreaName(i) == hitArenaName) {
                var drawId = this._modelSetting.getHitAreaId(i);
                return this.isHit(drawId, x, y);
            }
        }
        return false;
    };
    LAppModel.prototype.preLoadMotionGroup = function (group) {
        var _this = this;
        var _loop_3 = function (i) {
            var motionFileName = this_2._modelSetting.getMotionFileName(group, i);
            var name_2 = group + "_" + i;
            if (this_2._debugMode) {
                lapppal_1.LAppPal.printMessage("[APP]load motion: " + motionFileName + " => [" + name_2 + "]");
            }
            fetch("" + this_2._modelHomeDir + motionFileName)
                .then(function (response) { return response.arrayBuffer(); })
                .then(function (arrayBuffer) {
                var tmpMotion = _this.loadMotion(arrayBuffer, arrayBuffer.byteLength, name_2);
                var fadeTime = _this._modelSetting.getMotionFadeInTimeValue(group, i);
                if (fadeTime >= 0.0) {
                    tmpMotion.setFadeInTime(fadeTime);
                }
                fadeTime = _this._modelSetting.getMotionFadeOutTimeValue(group, i);
                if (fadeTime >= 0.0) {
                    tmpMotion.setFadeOutTime(fadeTime);
                }
                tmpMotion.setEffectIds(_this._eyeBlinkIds, _this._lipSyncIds);
                if (_this._motions.getValue(name_2) != null) {
                    acubismmotion_1.ACubismMotion.delete(_this._motions.getValue(name_2));
                }
                _this._motions.setValue(name_2, tmpMotion);
                _this._motionCount++;
                if (_this._motionCount >= _this._allMotionCount) {
                    _this._state = LoadStep.LoadTexture;
                    _this._motionManager.stopAllMotions();
                    _this._updating = false;
                    _this._initialized = true;
                    _this.createRenderer();
                    _this.setupTextures();
                    _this.getRenderer().startUp(lappdelegate_1.gl);
                }
            });
        };
        var this_2 = this;
        for (var i = 0; i < this._modelSetting.getMotionCount(group); i++) {
            _loop_3(i);
        }
    };
    LAppModel.prototype.releaseMotions = function () {
        this._motions.clear();
    };
    LAppModel.prototype.releaseExpressions = function () {
        this._expressions.clear();
    };
    LAppModel.prototype.doDraw = function () {
        if (this._model == null)
            return;
        var viewport = [0, 0, lappdelegate_1.canvas.width, lappdelegate_1.canvas.height];
        this.getRenderer().setRenderState(lappdelegate_1.frameBuffer, viewport);
        this.getRenderer().drawModel();
    };
    LAppModel.prototype.draw = function (matrix) {
        if (this._model == null) {
            return;
        }
        if (this._state == LoadStep.CompleteSetup) {
            matrix.multiplyByMatrix(this._modelMatrix);
            this.getRenderer().setMvpMatrix(matrix);
            this.doDraw();
        }
    };
    return LAppModel;
}(cubismusermodel_1.CubismUserModel));
exports.LAppModel = LAppModel;


/***/ })

},
/******/ function(__webpack_require__) { // webpackRuntimeModules
/******/ 	"use strict";
/******/ 
/******/ 	/* webpack/runtime/getFullHash */
/******/ 	!function() {
/******/ 		__webpack_require__.h = function() { return "b0e3b6a8af1dcc53d390"; }
/******/ 	}();
/******/ 	
/******/ }
);
//# sourceMappingURL=data:application/json;charset=utf-8;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIndlYnBhY2s6Ly8vLi9zcmMvbGFwcG1vZGVsLnRzIiwid2VicGFjazovLy93ZWJwYWNrL3J1bnRpbWUvZ2V0RnVsbEhhc2giXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6Ijs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7OztBQU9BLGdGQUFzQjtBQUV0QixzSkFBK0U7QUFDL0UsZ0pBQTJFO0FBQzNFLGdJQUd3QztBQUl4Qyw2SUFBbUU7QUFFbkUsdUlBQW1FO0FBQ25FLG1JQUd5QztBQUV6QyxvS0FHb0Q7QUFDcEQsMEdBQWdEO0FBR2hELG1IQUFzRDtBQUN0RCwySEFBNkQ7QUFFN0QsOEZBQTJDO0FBQzNDLHdGQUF1RTtBQUN2RSx5RUFBb0M7QUFFcEMsMEdBQTBEO0FBRTFELElBQUssUUF3Qko7QUF4QkQsV0FBSyxRQUFRO0lBQ1gsbURBQVU7SUFDVixpREFBUztJQUNULHlEQUFhO0lBQ2IsMkRBQWM7SUFDZCxtRUFBa0I7SUFDbEIscURBQVc7SUFDWCw2REFBZTtJQUNmLCtDQUFRO0lBQ1IsdURBQVk7SUFDWix5REFBYTtJQUNiLHNEQUFXO0lBQ1gsd0RBQVk7SUFDWixnRUFBZ0I7SUFDaEIsZ0VBQWdCO0lBQ2hCLDhEQUFlO0lBQ2Ysc0RBQVc7SUFDWCxvREFBVTtJQUNWLDREQUFjO0lBQ2Qsb0VBQWtCO0lBQ2xCLG9FQUFrQjtJQUNsQixzREFBVztJQUNYLDhEQUFlO0lBQ2YsMERBQWE7QUFDZixDQUFDLEVBeEJJLFFBQVEsS0FBUixRQUFRLFFBd0JaO0FBTUQ7SUFBK0IsNkJBQWU7SUFvdkI1QztRQUFBLFlBQ0UsaUJBQU8sU0FnRFI7UUE5Q0MsS0FBSSxDQUFDLGFBQWEsR0FBRyxJQUFJLENBQUM7UUFDMUIsS0FBSSxDQUFDLGFBQWEsR0FBRyxJQUFJLENBQUM7UUFDMUIsS0FBSSxDQUFDLGdCQUFnQixHQUFHLEdBQUcsQ0FBQztRQUU1QixLQUFJLENBQUMsWUFBWSxHQUFHLElBQUkscUJBQVMsRUFBa0IsQ0FBQztRQUNwRCxLQUFJLENBQUMsV0FBVyxHQUFHLElBQUkscUJBQVMsRUFBa0IsQ0FBQztRQUVuRCxLQUFJLENBQUMsUUFBUSxHQUFHLElBQUksZUFBTSxFQUF5QixDQUFDO1FBQ3BELEtBQUksQ0FBQyxZQUFZLEdBQUcsSUFBSSxlQUFNLEVBQXlCLENBQUM7UUFFeEQsS0FBSSxDQUFDLFFBQVEsR0FBRyxJQUFJLHFCQUFTLEVBQVcsQ0FBQztRQUN6QyxLQUFJLENBQUMsU0FBUyxHQUFHLElBQUkscUJBQVMsRUFBVyxDQUFDO1FBRTFDLEtBQUksQ0FBQyxjQUFjLEdBQUcsdUNBQWUsQ0FBQyxZQUFZLEVBQUUsQ0FBQyxLQUFLLENBQ3hELG1EQUF3QixDQUFDLFdBQVcsQ0FDckMsQ0FBQztRQUNGLEtBQUksQ0FBQyxjQUFjLEdBQUcsdUNBQWUsQ0FBQyxZQUFZLEVBQUUsQ0FBQyxLQUFLLENBQ3hELG1EQUF3QixDQUFDLFdBQVcsQ0FDckMsQ0FBQztRQUNGLEtBQUksQ0FBQyxjQUFjLEdBQUcsdUNBQWUsQ0FBQyxZQUFZLEVBQUUsQ0FBQyxLQUFLLENBQ3hELG1EQUF3QixDQUFDLFdBQVcsQ0FDckMsQ0FBQztRQUNGLEtBQUksQ0FBQyxnQkFBZ0IsR0FBRyx1Q0FBZSxDQUFDLFlBQVksRUFBRSxDQUFDLEtBQUssQ0FDMUQsbURBQXdCLENBQUMsYUFBYSxDQUN2QyxDQUFDO1FBQ0YsS0FBSSxDQUFDLGdCQUFnQixHQUFHLHVDQUFlLENBQUMsWUFBWSxFQUFFLENBQUMsS0FBSyxDQUMxRCxtREFBd0IsQ0FBQyxhQUFhLENBQ3ZDLENBQUM7UUFDRixLQUFJLENBQUMsa0JBQWtCLEdBQUcsdUNBQWUsQ0FBQyxZQUFZLEVBQUUsQ0FBQyxLQUFLLENBQzVELG1EQUF3QixDQUFDLGVBQWUsQ0FDekMsQ0FBQztRQUVGLEtBQUksQ0FBQyxNQUFNLEdBQUcsUUFBUSxDQUFDLFVBQVUsQ0FBQztRQUNsQyxLQUFJLENBQUMsZ0JBQWdCLEdBQUcsQ0FBQyxDQUFDO1FBQzFCLEtBQUksQ0FBQyxhQUFhLEdBQUcsQ0FBQyxDQUFDO1FBQ3ZCLEtBQUksQ0FBQyxZQUFZLEdBQUcsQ0FBQyxDQUFDO1FBQ3RCLEtBQUksQ0FBQyxlQUFlLEdBQUcsQ0FBQyxDQUFDO1FBQ3pCLEtBQUksQ0FBQyxlQUFlLEdBQUcsSUFBSSx1Q0FBa0IsRUFBRSxDQUFDO1FBRWhELEtBQUksQ0FBQyxjQUFjLEdBQUcsRUFBRSxDQUFDO1FBQ3pCLElBQUssTUFBYyxDQUFDLE9BQU8sRUFBQztZQUN6QixNQUFjLENBQUMsT0FBTyxDQUFDLEVBQUUsQ0FBQyxRQUFRLEVBQUUsVUFBQyxnQkFBZ0I7Z0JBRXBELE1BQU0sQ0FBQyxNQUFNLENBQUMsS0FBSSxDQUFDLGNBQWMsRUFBRSxnQkFBZ0IsQ0FBQyxDQUFDO1lBQ3ZELENBQUMsQ0FBQyxDQUFDO1NBQ0o7O0lBQ0gsQ0FBQztJQS94Qk0sOEJBQVUsR0FBakIsVUFBa0IsR0FBVyxFQUFFLFFBQWdCO1FBQS9DLGlCQWlCQztRQWhCQyxJQUFJLENBQUMsYUFBYSxHQUFHLEdBQUcsQ0FBQztRQUV6QixLQUFLLENBQUMsS0FBRyxJQUFJLENBQUMsYUFBYSxHQUFHLFFBQVUsQ0FBQzthQUN0QyxJQUFJLENBQUMsa0JBQVEsSUFBSSxlQUFRLENBQUMsV0FBVyxFQUFFLEVBQXRCLENBQXNCLENBQUM7YUFDeEMsSUFBSSxDQUFDLHFCQUFXO1lBQ2YsSUFBTSxPQUFPLEdBQXdCLElBQUksK0NBQXNCLENBQzdELFdBQVcsRUFDWCxXQUFXLENBQUMsVUFBVSxDQUN2QixDQUFDO1lBR0YsS0FBSSxDQUFDLE1BQU0sR0FBRyxRQUFRLENBQUMsU0FBUyxDQUFDO1lBR2pDLEtBQUksQ0FBQyxVQUFVLENBQUMsT0FBTyxDQUFDLENBQUM7UUFDM0IsQ0FBQyxDQUFDLENBQUM7SUFDUCxDQUFDO0lBUU8sOEJBQVUsR0FBbEIsVUFBbUIsT0FBNEI7UUFBL0MsaUJBNlFDO1FBNVFDLElBQUksQ0FBQyxTQUFTLEdBQUcsSUFBSSxDQUFDO1FBQ3RCLElBQUksQ0FBQyxZQUFZLEdBQUcsS0FBSyxDQUFDO1FBRTFCLElBQUksQ0FBQyxhQUFhLEdBQUcsT0FBTyxDQUFDO1FBRzdCLElBQUksSUFBSSxDQUFDLGFBQWEsQ0FBQyxnQkFBZ0IsRUFBRSxJQUFJLEVBQUUsRUFBRTtZQUMvQyxJQUFNLGFBQWEsR0FBRyxJQUFJLENBQUMsYUFBYSxDQUFDLGdCQUFnQixFQUFFLENBQUM7WUFFNUQsS0FBSyxDQUFDLEtBQUcsSUFBSSxDQUFDLGFBQWEsR0FBRyxhQUFlLENBQUM7aUJBQzNDLElBQUksQ0FBQyxrQkFBUSxJQUFJLGVBQVEsQ0FBQyxXQUFXLEVBQUUsRUFBdEIsQ0FBc0IsQ0FBQztpQkFDeEMsSUFBSSxDQUFDLHFCQUFXO2dCQUNmLEtBQUksQ0FBQyxTQUFTLENBQUMsV0FBVyxDQUFDLENBQUM7Z0JBQzVCLEtBQUksQ0FBQyxNQUFNLEdBQUcsUUFBUSxDQUFDLGNBQWMsQ0FBQztnQkFHdEMsb0JBQW9CLEVBQUUsQ0FBQztZQUN6QixDQUFDLENBQUMsQ0FBQztZQUVMLElBQUksQ0FBQyxNQUFNLEdBQUcsUUFBUSxDQUFDLGFBQWEsQ0FBQztTQUN0QzthQUFNO1lBQ0wsaUJBQU8sQ0FBQyxZQUFZLENBQUMsNEJBQTRCLENBQUMsQ0FBQztTQUNwRDtRQUdELElBQU0sb0JBQW9CLEdBQUc7WUFDM0IsSUFBSSxLQUFJLENBQUMsYUFBYSxDQUFDLGtCQUFrQixFQUFFLEdBQUcsQ0FBQyxFQUFFO2dCQUMvQyxJQUFNLE9BQUssR0FBVyxLQUFJLENBQUMsYUFBYSxDQUFDLGtCQUFrQixFQUFFLENBQUM7d0NBRXJELENBQUM7b0JBQ1IsSUFBTSxjQUFjLEdBQUcsS0FBSSxDQUFDLGFBQWEsQ0FBQyxpQkFBaUIsQ0FBQyxDQUFDLENBQUMsQ0FBQztvQkFDL0QsSUFBTSxrQkFBa0IsR0FBRyxLQUFJLENBQUMsYUFBYSxDQUFDLHFCQUFxQixDQUNqRSxDQUFDLENBQ0YsQ0FBQztvQkFFRixLQUFLLENBQUMsS0FBRyxLQUFJLENBQUMsYUFBYSxHQUFHLGtCQUFvQixDQUFDO3lCQUNoRCxJQUFJLENBQUMsa0JBQVEsSUFBSSxlQUFRLENBQUMsV0FBVyxFQUFFLEVBQXRCLENBQXNCLENBQUM7eUJBQ3hDLElBQUksQ0FBQyxxQkFBVzt3QkFDZixJQUFNLE1BQU0sR0FBa0IsS0FBSSxDQUFDLGNBQWMsQ0FDL0MsV0FBVyxFQUNYLFdBQVcsQ0FBQyxVQUFVLEVBQ3RCLGNBQWMsQ0FDZixDQUFDO3dCQUVGLElBQUksS0FBSSxDQUFDLFlBQVksQ0FBQyxRQUFRLENBQUMsY0FBYyxDQUFDLElBQUksSUFBSSxFQUFFOzRCQUN0RCw2QkFBYSxDQUFDLE1BQU0sQ0FDbEIsS0FBSSxDQUFDLFlBQVksQ0FBQyxRQUFRLENBQUMsY0FBYyxDQUFDLENBQzNDLENBQUM7NEJBQ0YsS0FBSSxDQUFDLFlBQVksQ0FBQyxRQUFRLENBQUMsY0FBYyxFQUFFLElBQUksQ0FBQyxDQUFDO3lCQUNsRDt3QkFFRCxLQUFJLENBQUMsWUFBWSxDQUFDLFFBQVEsQ0FBQyxjQUFjLEVBQUUsTUFBTSxDQUFDLENBQUM7d0JBRW5ELEtBQUksQ0FBQyxnQkFBZ0IsRUFBRSxDQUFDO3dCQUV4QixJQUFJLEtBQUksQ0FBQyxnQkFBZ0IsSUFBSSxPQUFLLEVBQUU7NEJBQ2xDLEtBQUksQ0FBQyxNQUFNLEdBQUcsUUFBUSxDQUFDLFdBQVcsQ0FBQzs0QkFHbkMsaUJBQWlCLEVBQUUsQ0FBQzt5QkFDckI7b0JBQ0gsQ0FBQyxDQUFDLENBQUM7O2dCQWhDUCxLQUFLLElBQUksQ0FBQyxHQUFHLENBQUMsRUFBRSxDQUFDLEdBQUcsT0FBSyxFQUFFLENBQUMsRUFBRTs0QkFBckIsQ0FBQztpQkFpQ1Q7Z0JBQ0QsS0FBSSxDQUFDLE1BQU0sR0FBRyxRQUFRLENBQUMsa0JBQWtCLENBQUM7YUFDM0M7aUJBQU07Z0JBQ0wsS0FBSSxDQUFDLE1BQU0sR0FBRyxRQUFRLENBQUMsV0FBVyxDQUFDO2dCQUduQyxpQkFBaUIsRUFBRSxDQUFDO2FBQ3JCO1FBQ0gsQ0FBQyxDQUFDO1FBR0YsSUFBTSxpQkFBaUIsR0FBRztZQUN4QixJQUFJLEtBQUksQ0FBQyxhQUFhLENBQUMsa0JBQWtCLEVBQUUsSUFBSSxFQUFFLEVBQUU7Z0JBQ2pELElBQU0sZUFBZSxHQUFHLEtBQUksQ0FBQyxhQUFhLENBQUMsa0JBQWtCLEVBQUUsQ0FBQztnQkFFaEUsS0FBSyxDQUFDLEtBQUcsS0FBSSxDQUFDLGFBQWEsR0FBRyxlQUFpQixDQUFDO3FCQUM3QyxJQUFJLENBQUMsa0JBQVEsSUFBSSxlQUFRLENBQUMsV0FBVyxFQUFFLEVBQXRCLENBQXNCLENBQUM7cUJBQ3hDLElBQUksQ0FBQyxxQkFBVztvQkFDZixLQUFJLENBQUMsV0FBVyxDQUFDLFdBQVcsRUFBRSxXQUFXLENBQUMsVUFBVSxDQUFDLENBQUM7b0JBRXRELEtBQUksQ0FBQyxNQUFNLEdBQUcsUUFBUSxDQUFDLFFBQVEsQ0FBQztvQkFHaEMsY0FBYyxFQUFFLENBQUM7Z0JBQ25CLENBQUMsQ0FBQyxDQUFDO2dCQUNMLEtBQUksQ0FBQyxNQUFNLEdBQUcsUUFBUSxDQUFDLGVBQWUsQ0FBQzthQUN4QztpQkFBTTtnQkFDTCxLQUFJLENBQUMsTUFBTSxHQUFHLFFBQVEsQ0FBQyxRQUFRLENBQUM7Z0JBR2hDLGNBQWMsRUFBRSxDQUFDO2FBQ2xCO1FBQ0gsQ0FBQyxDQUFDO1FBR0YsSUFBTSxjQUFjLEdBQUc7WUFDckIsSUFBSSxLQUFJLENBQUMsYUFBYSxDQUFDLGVBQWUsRUFBRSxJQUFJLEVBQUUsRUFBRTtnQkFDOUMsSUFBTSxZQUFZLEdBQUcsS0FBSSxDQUFDLGFBQWEsQ0FBQyxlQUFlLEVBQUUsQ0FBQztnQkFFMUQsS0FBSyxDQUFDLEtBQUcsS0FBSSxDQUFDLGFBQWEsR0FBRyxZQUFjLENBQUM7cUJBQzFDLElBQUksQ0FBQyxrQkFBUSxJQUFJLGVBQVEsQ0FBQyxXQUFXLEVBQUUsRUFBdEIsQ0FBc0IsQ0FBQztxQkFDeEMsSUFBSSxDQUFDLHFCQUFXO29CQUNmLEtBQUksQ0FBQyxRQUFRLENBQUMsV0FBVyxFQUFFLFdBQVcsQ0FBQyxVQUFVLENBQUMsQ0FBQztvQkFFbkQsS0FBSSxDQUFDLE1BQU0sR0FBRyxRQUFRLENBQUMsYUFBYSxDQUFDO29CQUdyQyxhQUFhLEVBQUUsQ0FBQztnQkFDbEIsQ0FBQyxDQUFDLENBQUM7Z0JBQ0wsS0FBSSxDQUFDLE1BQU0sR0FBRyxRQUFRLENBQUMsWUFBWSxDQUFDO2FBQ3JDO2lCQUFNO2dCQUNMLEtBQUksQ0FBQyxNQUFNLEdBQUcsUUFBUSxDQUFDLGFBQWEsQ0FBQztnQkFHckMsYUFBYSxFQUFFLENBQUM7YUFDakI7UUFDSCxDQUFDLENBQUM7UUFHRixJQUFNLGFBQWEsR0FBRztZQU9wQixXQUFXLEVBQUUsQ0FBQztRQUNoQixDQUFDLENBQUM7UUFHRixJQUFNLFdBQVcsR0FBRztZQUNsQixLQUFJLENBQUMsT0FBTyxHQUFHLDJCQUFZLENBQUMsTUFBTSxFQUFFLENBQUM7WUFFckMsSUFBTSxnQkFBZ0IsR0FBbUMsSUFBSSxxQkFBUyxFQUFFLENBQUM7WUFhekUsZ0JBQWdCLENBQUMsUUFBUSxDQUN2QixJQUFJLGtDQUFtQixDQUNyQix1Q0FBZSxDQUFDLFlBQVksRUFBRSxDQUFDLEtBQUssQ0FDbEMsbURBQXdCLENBQUMsV0FBVyxDQUNyQyxFQUNELEdBQUcsRUFDSCxHQUFHLEVBQ0gsTUFBTSxFQUNOLEdBQUcsQ0FDSixDQUNGLENBQUM7WUFFRixLQUFJLENBQUMsT0FBTyxDQUFDLGFBQWEsQ0FBQyxnQkFBZ0IsQ0FBQyxDQUFDO1lBQzdDLEtBQUksQ0FBQyxNQUFNLEdBQUcsUUFBUSxDQUFDLFlBQVksQ0FBQztZQUdwQyxZQUFZLEVBQUUsQ0FBQztRQUNqQixDQUFDLENBQUM7UUFHRixJQUFNLFlBQVksR0FBRztZQUNuQixJQUFJLEtBQUksQ0FBQyxhQUFhLENBQUMsZUFBZSxFQUFFLElBQUksRUFBRSxFQUFFO2dCQUM5QyxJQUFNLFlBQVksR0FBRyxLQUFJLENBQUMsYUFBYSxDQUFDLGVBQWUsRUFBRSxDQUFDO2dCQUUxRCxLQUFLLENBQUMsS0FBRyxLQUFJLENBQUMsYUFBYSxHQUFHLFlBQWMsQ0FBQztxQkFDMUMsSUFBSSxDQUFDLGtCQUFRLElBQUksZUFBUSxDQUFDLFdBQVcsRUFBRSxFQUF0QixDQUFzQixDQUFDO3FCQUN4QyxJQUFJLENBQUMscUJBQVc7b0JBQ2YsS0FBSSxDQUFDLFlBQVksQ0FBQyxXQUFXLEVBQUUsV0FBVyxDQUFDLFVBQVUsQ0FBQyxDQUFDO29CQUV2RCxLQUFJLENBQUMsTUFBTSxHQUFHLFFBQVEsQ0FBQyxnQkFBZ0IsQ0FBQztvQkFHeEMsZ0JBQWdCLEVBQUUsQ0FBQztnQkFDckIsQ0FBQyxDQUFDLENBQUM7Z0JBRUwsS0FBSSxDQUFDLE1BQU0sR0FBRyxRQUFRLENBQUMsZ0JBQWdCLENBQUM7YUFDekM7aUJBQU07Z0JBQ0wsS0FBSSxDQUFDLE1BQU0sR0FBRyxRQUFRLENBQUMsZ0JBQWdCLENBQUM7Z0JBR3hDLGdCQUFnQixFQUFFLENBQUM7YUFDcEI7UUFDSCxDQUFDLENBQUM7UUFHRixJQUFNLGdCQUFnQixHQUFHO1lBQ3ZCLElBQU0sZUFBZSxHQUFXLEtBQUksQ0FBQyxhQUFhLENBQUMseUJBQXlCLEVBQUUsQ0FBQztZQUUvRSxLQUFLLElBQUksQ0FBQyxHQUFHLENBQUMsRUFBRSxDQUFDLEdBQUcsZUFBZSxFQUFFLEVBQUUsQ0FBQyxFQUFFO2dCQUN4QyxLQUFJLENBQUMsWUFBWSxDQUFDLFFBQVEsQ0FDeEIsS0FBSSxDQUFDLGFBQWEsQ0FBQyxzQkFBc0IsQ0FBQyxDQUFDLENBQUMsQ0FDN0MsQ0FBQzthQUNIO1lBRUQsS0FBSSxDQUFDLE1BQU0sR0FBRyxRQUFRLENBQUMsZUFBZSxDQUFDO1lBR3ZDLGVBQWUsRUFBRSxDQUFDO1FBQ3BCLENBQUMsQ0FBQztRQUdGLElBQU0sZUFBZSxHQUFHO1lBQ3RCLElBQU0sY0FBYyxHQUFHLEtBQUksQ0FBQyxhQUFhLENBQUMsd0JBQXdCLEVBQUUsQ0FBQztZQUVyRSxLQUFLLElBQUksQ0FBQyxHQUFHLENBQUMsRUFBRSxDQUFDLEdBQUcsY0FBYyxFQUFFLEVBQUUsQ0FBQyxFQUFFO2dCQUN2QyxLQUFJLENBQUMsV0FBVyxDQUFDLFFBQVEsQ0FBQyxLQUFJLENBQUMsYUFBYSxDQUFDLHFCQUFxQixDQUFDLENBQUMsQ0FBQyxDQUFDLENBQUM7YUFDeEU7WUFDRCxLQUFJLENBQUMsTUFBTSxHQUFHLFFBQVEsQ0FBQyxXQUFXLENBQUM7WUFHbkMsV0FBVyxFQUFFLENBQUM7UUFDaEIsQ0FBQyxDQUFDO1FBR0YsSUFBTSxXQUFXLEdBQUc7WUFDbEIsSUFBTSxNQUFNLEdBQTJCLElBQUksZUFBTSxFQUFrQixDQUFDO1lBQ3BFLEtBQUksQ0FBQyxhQUFhLENBQUMsWUFBWSxDQUFDLE1BQU0sQ0FBQyxDQUFDO1lBQ3hDLEtBQUksQ0FBQyxZQUFZLENBQUMsZUFBZSxDQUFDLE1BQU0sQ0FBQyxDQUFDO1lBQzFDLEtBQUksQ0FBQyxNQUFNLEdBQUcsUUFBUSxDQUFDLFVBQVUsQ0FBQztZQUdsQyxnQkFBZ0IsRUFBRSxDQUFDO1FBQ3JCLENBQUMsQ0FBQztRQUdGLElBQU0sZ0JBQWdCLEdBQUc7WUFDdkIsS0FBSSxDQUFDLE1BQU0sR0FBRyxRQUFRLENBQUMsY0FBYyxDQUFDO1lBQ3RDLEtBQUksQ0FBQyxNQUFNLENBQUMsY0FBYyxFQUFFLENBQUM7WUFDN0IsS0FBSSxDQUFDLGVBQWUsR0FBRyxDQUFDLENBQUM7WUFDekIsS0FBSSxDQUFDLFlBQVksR0FBRyxDQUFDLENBQUM7WUFDdEIsSUFBTSxLQUFLLEdBQWEsRUFBRSxDQUFDO1lBRTNCLElBQU0sZ0JBQWdCLEdBQVcsS0FBSSxDQUFDLGFBQWEsQ0FBQyxtQkFBbUIsRUFBRSxDQUFDO1lBRzFFLEtBQUssSUFBSSxDQUFDLEdBQUcsQ0FBQyxFQUFFLENBQUMsR0FBRyxnQkFBZ0IsRUFBRSxDQUFDLEVBQUUsRUFBRTtnQkFDekMsS0FBSyxDQUFDLENBQUMsQ0FBQyxHQUFHLEtBQUksQ0FBQyxhQUFhLENBQUMsa0JBQWtCLENBQUMsQ0FBQyxDQUFDLENBQUM7Z0JBQ3BELEtBQUksQ0FBQyxlQUFlLElBQUksS0FBSSxDQUFDLGFBQWEsQ0FBQyxjQUFjLENBQUMsS0FBSyxDQUFDLENBQUMsQ0FBQyxDQUFDLENBQUM7YUFDckU7WUFHRCxLQUFLLElBQUksQ0FBQyxHQUFHLENBQUMsRUFBRSxDQUFDLEdBQUcsZ0JBQWdCLEVBQUUsQ0FBQyxFQUFFLEVBQUU7Z0JBQ3pDLEtBQUksQ0FBQyxrQkFBa0IsQ0FBQyxLQUFLLENBQUMsQ0FBQyxDQUFDLENBQUMsQ0FBQzthQUNuQztZQUdELElBQUksZ0JBQWdCLElBQUksQ0FBQyxFQUFFO2dCQUN6QixLQUFJLENBQUMsTUFBTSxHQUFHLFFBQVEsQ0FBQyxXQUFXLENBQUM7Z0JBR25DLEtBQUksQ0FBQyxjQUFjLENBQUMsY0FBYyxFQUFFLENBQUM7Z0JBRXJDLEtBQUksQ0FBQyxTQUFTLEdBQUcsS0FBSyxDQUFDO2dCQUN2QixLQUFJLENBQUMsWUFBWSxHQUFHLElBQUksQ0FBQztnQkFFekIsS0FBSSxDQUFDLGNBQWMsRUFBRSxDQUFDO2dCQUN0QixLQUFJLENBQUMsYUFBYSxFQUFFLENBQUM7Z0JBQ3JCLEtBQUksQ0FBQyxXQUFXLEVBQUUsQ0FBQyxPQUFPLENBQUMsaUJBQUUsQ0FBQyxDQUFDO2FBQ2hDO1FBQ0gsQ0FBQyxDQUFDO0lBQ0osQ0FBQztJQUtPLGlDQUFhLEdBQXJCO1FBQUEsaUJBOENDO1FBNUNDLElBQU0sY0FBYyxHQUFHLElBQUksQ0FBQztRQUU1QixJQUFJLElBQUksQ0FBQyxNQUFNLElBQUksUUFBUSxDQUFDLFdBQVcsRUFBRTtZQUV2QyxJQUFNLGNBQVksR0FBVyxJQUFJLENBQUMsYUFBYSxDQUFDLGVBQWUsRUFBRSxDQUFDO29DQUc1RCxrQkFBa0I7Z0JBS3RCLElBQUksT0FBSyxhQUFhLENBQUMsa0JBQWtCLENBQUMsa0JBQWtCLENBQUMsSUFBSSxFQUFFLEVBQUU7b0JBQ25FLE9BQU8sQ0FBQyxHQUFHLENBQUMseUJBQXlCLENBQUMsQ0FBQzs7aUJBRXhDO2dCQUdELElBQUksV0FBVyxHQUFHLE9BQUssYUFBYSxDQUFDLGtCQUFrQixDQUNyRCxrQkFBa0IsQ0FDbkIsQ0FBQztnQkFDRixXQUFXLEdBQUcsT0FBSyxhQUFhLEdBQUcsV0FBVyxDQUFDO2dCQUcvQyxJQUFNLE1BQU0sR0FBRyxVQUFDLFdBQXdCO29CQUN0QyxLQUFJLENBQUMsV0FBVyxFQUFFLENBQUMsV0FBVyxDQUFDLGtCQUFrQixFQUFFLFdBQVcsQ0FBQyxFQUFFLENBQUMsQ0FBQztvQkFFbkUsS0FBSSxDQUFDLGFBQWEsRUFBRSxDQUFDO29CQUVyQixJQUFJLEtBQUksQ0FBQyxhQUFhLElBQUksY0FBWSxFQUFFO3dCQUV0QyxLQUFJLENBQUMsTUFBTSxHQUFHLFFBQVEsQ0FBQyxhQUFhLENBQUM7cUJBQ3RDO2dCQUNILENBQUMsQ0FBQztnQkFHRiwyQkFBWSxDQUFDLFdBQVcsRUFBRTtxQkFDdkIsaUJBQWlCLEVBQUU7cUJBQ25CLHdCQUF3QixDQUFDLFdBQVcsRUFBRSxjQUFjLEVBQUUsTUFBTSxDQUFDLENBQUM7Z0JBQ2pFLE9BQUssV0FBVyxFQUFFLENBQUMsdUJBQXVCLENBQUMsY0FBYyxDQUFDLENBQUM7OztZQWpDN0QsS0FDRSxJQUFJLGtCQUFrQixHQUFHLENBQUMsRUFDMUIsa0JBQWtCLEdBQUcsY0FBWSxFQUNqQyxrQkFBa0IsRUFBRTt3QkFGaEIsa0JBQWtCO2FBaUN2QjtZQUVELElBQUksQ0FBQyxNQUFNLEdBQUcsUUFBUSxDQUFDLGVBQWUsQ0FBQztTQUN4QztJQUNILENBQUM7SUFLTSxrQ0FBYyxHQUFyQjtRQUNFLElBQUksQ0FBQyxjQUFjLEVBQUUsQ0FBQztRQUN0QixJQUFJLENBQUMsY0FBYyxFQUFFLENBQUM7UUFDdEIsSUFBSSxDQUFDLGFBQWEsRUFBRSxDQUFDO0lBQ3ZCLENBQUM7SUFLTSwwQkFBTSxHQUFiO1FBQ0UsSUFBSSxJQUFJLENBQUMsTUFBTSxJQUFJLFFBQVEsQ0FBQyxhQUFhO1lBQUUsT0FBTztRQUVsRCxJQUFNLGdCQUFnQixHQUFXLGlCQUFPLENBQUMsWUFBWSxFQUFFLENBQUM7UUFDeEQsSUFBSSxDQUFDLGdCQUFnQixJQUFJLGdCQUFnQixDQUFDO1FBRTFDLElBQUksQ0FBQyxZQUFZLENBQUMsTUFBTSxDQUFDLGdCQUFnQixDQUFDLENBQUM7UUFDM0MsSUFBSSxDQUFDLE1BQU0sR0FBRyxJQUFJLENBQUMsWUFBWSxDQUFDLElBQUksRUFBRSxDQUFDO1FBQ3ZDLElBQUksQ0FBQyxNQUFNLEdBQUcsSUFBSSxDQUFDLFlBQVksQ0FBQyxJQUFJLEVBQUUsQ0FBQztRQUd2QyxJQUFJLGFBQWEsR0FBRyxLQUFLLENBQUM7UUFHMUIsSUFBSSxDQUFDLE1BQU0sQ0FBQyxjQUFjLEVBQUUsQ0FBQztRQWlCN0IsSUFBSSxDQUFDLGFBQWEsRUFBRTtZQUNsQixJQUFJLElBQUksQ0FBQyxTQUFTLElBQUksSUFBSSxFQUFFO2dCQUUxQixJQUFJLENBQUMsU0FBUyxDQUFDLGdCQUFnQixDQUFDLElBQUksQ0FBQyxNQUFNLEVBQUUsZ0JBQWdCLENBQUMsQ0FBQzthQUNoRTtTQUNGO1FBRUQsSUFBSSxJQUFJLENBQUMsa0JBQWtCLElBQUksSUFBSSxFQUFFO1lBQ25DLElBQUksQ0FBQyxrQkFBa0IsQ0FBQyxZQUFZLENBQUMsSUFBSSxDQUFDLE1BQU0sRUFBRSxnQkFBZ0IsQ0FBQyxDQUFDO1NBQ3JFO1FBc0JELElBQUksVUFBVSxHQUFpQixNQUFNLENBQUMsSUFBSSxDQUFDLElBQUksQ0FBQyxjQUFjLENBQUM7UUFDL0QsS0FDRSxJQUFJLENBQUMsR0FBRyxDQUFDLEVBQUUsS0FBSyxXQUNoQixLQUFLLEdBQUcsVUFBVSxDQUFDLENBQUMsQ0FBQyxFQUNyQixDQUFDLEVBQUUsRUFBRSxLQUFLLENBQUMsVUFBVSxDQUFDLE9BQU8sQ0FBQyxJQUFJLElBQUksQ0FBQyxNQUFNLENBQUMscUJBQXFCLENBQ2pFLHVDQUFlLENBQUMsWUFBWSxFQUFFLENBQUMsS0FBSyxDQUFDLEtBQUssQ0FBQyxFQUMzQyxJQUFJLENBQUMsY0FBYyxDQUFDLEtBQUssQ0FBQyxDQUMzQjtZQUNGLENBQUM7UUFHRixJQUFJLElBQUksQ0FBQyxPQUFPLElBQUksSUFBSSxFQUFFO1lBQ3hCLElBQUksQ0FBQyxPQUFPLENBQUMsZ0JBQWdCLENBQUMsSUFBSSxDQUFDLE1BQU0sRUFBRSxnQkFBZ0IsQ0FBQyxDQUFDO1NBQzlEO1FBR0QsSUFBSSxJQUFJLENBQUMsUUFBUSxJQUFJLElBQUksRUFBRTtZQUN6QixJQUFJLENBQUMsUUFBUSxDQUFDLFFBQVEsQ0FBQyxJQUFJLENBQUMsTUFBTSxFQUFFLGdCQUFnQixDQUFDLENBQUM7U0FDdkQ7UUFHRCxJQUFJLElBQUksQ0FBQyxRQUFRLEVBQUU7WUFDakIsSUFBSSxLQUFLLEdBQUcsR0FBRyxDQUFDO1lBRWhCLElBQUksQ0FBQyxlQUFlLENBQUMsTUFBTSxDQUFDLGdCQUFnQixDQUFDLENBQUM7WUFDOUMsS0FBSyxHQUFHLElBQUksQ0FBQyxlQUFlLENBQUMsTUFBTSxFQUFFLENBQUM7WUFFdEMsS0FBSyxJQUFJLENBQUMsR0FBRyxDQUFDLEVBQUUsQ0FBQyxHQUFHLElBQUksQ0FBQyxXQUFXLENBQUMsT0FBTyxFQUFFLEVBQUUsRUFBRSxDQUFDLEVBQUU7Z0JBQ25ELElBQUksQ0FBQyxNQUFNLENBQUMscUJBQXFCLENBQUMsSUFBSSxDQUFDLFdBQVcsQ0FBQyxFQUFFLENBQUMsQ0FBQyxDQUFDLEVBQUUsS0FBSyxFQUFFLEdBQUcsQ0FBQyxDQUFDO2FBQ3ZFO1NBQ0Y7UUFHRCxJQUFJLElBQUksQ0FBQyxLQUFLLElBQUksSUFBSSxFQUFFO1lBQ3RCLElBQUksQ0FBQyxLQUFLLENBQUMsZ0JBQWdCLENBQUMsSUFBSSxDQUFDLE1BQU0sRUFBRSxnQkFBZ0IsQ0FBQyxDQUFDO1NBQzVEO1FBRUQsSUFBSSxDQUFDLE1BQU0sQ0FBQyxNQUFNLEVBQUUsQ0FBQztJQUN2QixDQUFDO0lBVU0sK0JBQVcsR0FBbEIsVUFDRSxLQUFhLEVBQ2IsRUFBVSxFQUNWLFFBQWdCLEVBQ2hCLHVCQUFnRDtRQUpsRCxpQkFxRUM7UUEvREMsSUFBSSxRQUFRLElBQUksVUFBVSxDQUFDLGFBQWEsRUFBRTtZQUN4QyxJQUFJLENBQUMsY0FBYyxDQUFDLGtCQUFrQixDQUFDLFFBQVEsQ0FBQyxDQUFDO1NBQ2xEO2FBQU0sSUFBSSxDQUFDLElBQUksQ0FBQyxjQUFjLENBQUMsYUFBYSxDQUFDLFFBQVEsQ0FBQyxFQUFFO1lBQ3ZELElBQUksSUFBSSxDQUFDLFVBQVUsRUFBRTtnQkFDbkIsaUJBQU8sQ0FBQyxZQUFZLENBQUMsMEJBQTBCLENBQUMsQ0FBQzthQUNsRDtZQUNELE9BQU8sNkRBQWtDLENBQUM7U0FDM0M7UUFFRCxJQUFNLGNBQWMsR0FBRyxJQUFJLENBQUMsYUFBYSxDQUFDLGlCQUFpQixDQUFDLEtBQUssRUFBRSxFQUFFLENBQUMsQ0FBQztRQUd2RSxJQUFNLElBQUksR0FBTSxLQUFLLFNBQUksRUFBSSxDQUFDO1FBQzlCLElBQUksTUFBTSxHQUFpQixJQUFJLENBQUMsUUFBUSxDQUFDLFFBQVEsQ0FBQyxJQUFJLENBQWlCLENBQUM7UUFDeEUsSUFBSSxVQUFVLEdBQUcsS0FBSyxDQUFDO1FBRXZCLElBQUksTUFBTSxJQUFJLElBQUksRUFBRTtZQUNsQixLQUFLLENBQUMsS0FBRyxJQUFJLENBQUMsYUFBYSxHQUFHLGNBQWdCLENBQUM7aUJBQzVDLElBQUksQ0FBQyxrQkFBUSxJQUFJLGVBQVEsQ0FBQyxXQUFXLEVBQUUsRUFBdEIsQ0FBc0IsQ0FBQztpQkFDeEMsSUFBSSxDQUFDLHFCQUFXO2dCQUNmLE1BQU0sR0FBRyxLQUFJLENBQUMsVUFBVSxDQUN0QixXQUFXLEVBQ1gsV0FBVyxDQUFDLFVBQVUsRUFDdEIsSUFBSSxFQUNKLHVCQUF1QixDQUN4QixDQUFDO2dCQUNGLElBQUksUUFBUSxHQUFXLEtBQUksQ0FBQyxhQUFhLENBQUMsd0JBQXdCLENBQ2hFLEtBQUssRUFDTCxFQUFFLENBQ0gsQ0FBQztnQkFFRixJQUFJLFFBQVEsSUFBSSxHQUFHLEVBQUU7b0JBQ25CLE1BQU0sQ0FBQyxhQUFhLENBQUMsUUFBUSxDQUFDLENBQUM7aUJBQ2hDO2dCQUVELFFBQVEsR0FBRyxLQUFJLENBQUMsYUFBYSxDQUFDLHlCQUF5QixDQUFDLEtBQUssRUFBRSxFQUFFLENBQUMsQ0FBQztnQkFDbkUsSUFBSSxRQUFRLElBQUksR0FBRyxFQUFFO29CQUNuQixNQUFNLENBQUMsY0FBYyxDQUFDLFFBQVEsQ0FBQyxDQUFDO2lCQUNqQztnQkFFRCxNQUFNLENBQUMsWUFBWSxDQUFDLEtBQUksQ0FBQyxZQUFZLEVBQUUsS0FBSSxDQUFDLFdBQVcsQ0FBQyxDQUFDO2dCQUN6RCxVQUFVLEdBQUcsSUFBSSxDQUFDO1lBQ3BCLENBQUMsQ0FBQyxDQUFDO1NBQ047YUFBTTtZQUNMLE1BQU0sQ0FBQyx3QkFBd0IsQ0FBQyx1QkFBdUIsQ0FBQyxDQUFDO1NBQzFEO1FBR0QsSUFBTSxLQUFLLEdBQUcsSUFBSSxDQUFDLGFBQWEsQ0FBQyxzQkFBc0IsQ0FBQyxLQUFLLEVBQUUsRUFBRSxDQUFDLENBQUM7UUFDbkUsSUFBSSxLQUFLLENBQUMsYUFBYSxDQUFDLEVBQUUsQ0FBQyxJQUFJLENBQUMsRUFBRTtZQUNoQyxJQUFJLElBQUksR0FBRyxLQUFLLENBQUM7WUFDakIsSUFBSSxHQUFHLElBQUksQ0FBQyxhQUFhLEdBQUcsSUFBSSxDQUFDO1lBQ2pDLElBQUksQ0FBQyxlQUFlLENBQUMsS0FBSyxDQUFDLElBQUksQ0FBQyxDQUFDO1NBQ2xDO1FBRUQsSUFBSSxJQUFJLENBQUMsVUFBVSxFQUFFO1lBQ25CLGlCQUFPLENBQUMsWUFBWSxDQUFDLHlCQUF1QixLQUFLLFNBQUksRUFBSSxDQUFDLENBQUM7U0FDNUQ7UUFDRCxPQUFPLElBQUksQ0FBQyxjQUFjLENBQUMsbUJBQW1CLENBQzVDLE1BQU0sRUFDTixVQUFVLEVBQ1YsUUFBUSxDQUNULENBQUM7SUFDSixDQUFDO0lBU00scUNBQWlCLEdBQXhCLFVBQ0UsS0FBYSxFQUNiLFFBQWdCLEVBQ2hCLHVCQUFnRDtRQUVoRCxJQUFJLElBQUksQ0FBQyxhQUFhLENBQUMsY0FBYyxDQUFDLEtBQUssQ0FBQyxJQUFJLENBQUMsRUFBRTtZQUNqRCxPQUFPLDZEQUFrQyxDQUFDO1NBQzNDO1FBRUQsSUFBTSxFQUFFLEdBQVcsSUFBSSxDQUFDLEtBQUssQ0FDM0IsSUFBSSxDQUFDLE1BQU0sRUFBRSxHQUFHLElBQUksQ0FBQyxhQUFhLENBQUMsY0FBYyxDQUFDLEtBQUssQ0FBQyxDQUN6RCxDQUFDO1FBRUYsT0FBTyxJQUFJLENBQUMsV0FBVyxDQUFDLEtBQUssRUFBRSxFQUFFLEVBQUUsUUFBUSxFQUFFLHVCQUF1QixDQUFDLENBQUM7SUFDeEUsQ0FBQztJQU9NLGlDQUFhLEdBQXBCLFVBQXFCLFlBQW9CO1FBQ3ZDLElBQU0sTUFBTSxHQUFrQixJQUFJLENBQUMsWUFBWSxDQUFDLFFBQVEsQ0FBQyxZQUFZLENBQUMsQ0FBQztRQUV2RSxJQUFJLElBQUksQ0FBQyxVQUFVLEVBQUU7WUFDbkIsaUJBQU8sQ0FBQyxZQUFZLENBQUMsdUJBQXFCLFlBQVksTUFBRyxDQUFDLENBQUM7U0FDNUQ7UUFFRCxJQUFJLE1BQU0sSUFBSSxJQUFJLEVBQUU7WUFDbEIsSUFBSSxDQUFDLGtCQUFrQixDQUFDLG1CQUFtQixDQUN6QyxNQUFNLEVBQ04sS0FBSyxFQUNMLFVBQVUsQ0FBQyxhQUFhLENBQ3pCLENBQUM7U0FDSDthQUFNO1lBQ0wsSUFBSSxJQUFJLENBQUMsVUFBVSxFQUFFO2dCQUNuQixpQkFBTyxDQUFDLFlBQVksQ0FBQyxxQkFBbUIsWUFBWSxjQUFXLENBQUMsQ0FBQzthQUNsRTtTQUNGO0lBQ0gsQ0FBQztJQUtNLHVDQUFtQixHQUExQjtRQUNFLElBQUksSUFBSSxDQUFDLFlBQVksQ0FBQyxPQUFPLEVBQUUsSUFBSSxDQUFDLEVBQUU7WUFDcEMsT0FBTztTQUNSO1FBRUQsSUFBTSxFQUFFLEdBQVcsSUFBSSxDQUFDLEtBQUssQ0FBQyxJQUFJLENBQUMsTUFBTSxFQUFFLEdBQUcsSUFBSSxDQUFDLFlBQVksQ0FBQyxPQUFPLEVBQUUsQ0FBQyxDQUFDO1FBRTNFLEtBQUssSUFBSSxDQUFDLEdBQUcsQ0FBQyxFQUFFLENBQUMsR0FBRyxJQUFJLENBQUMsWUFBWSxDQUFDLE9BQU8sRUFBRSxFQUFFLENBQUMsRUFBRSxFQUFFO1lBQ3BELElBQUksQ0FBQyxJQUFJLEVBQUUsRUFBRTtnQkFDWCxJQUFNLE1BQUksR0FBVyxJQUFJLENBQUMsWUFBWSxDQUFDLFVBQVUsQ0FBQyxDQUFDLENBQUMsQ0FBQyxLQUFLLENBQUM7Z0JBQzNELElBQUksQ0FBQyxhQUFhLENBQUMsTUFBSSxDQUFDLENBQUM7Z0JBQ3pCLE9BQU87YUFDUjtTQUNGO0lBQ0gsQ0FBQztJQUtNLG9DQUFnQixHQUF2QixVQUF3QixVQUFxQjtRQUMzQywyQkFBYSxDQUFDLDZCQUE2QixFQUFFLFVBQVUsQ0FBQyxDQUFDLENBQUMsQ0FBQztJQUM3RCxDQUFDO0lBVU0sMkJBQU8sR0FBZCxVQUFlLFlBQW9CLEVBQUUsQ0FBUyxFQUFFLENBQVM7UUFFdkQsSUFBSSxJQUFJLENBQUMsUUFBUSxHQUFHLENBQUMsRUFBRTtZQUNyQixPQUFPLEtBQUssQ0FBQztTQUNkO1FBRUQsSUFBTSxLQUFLLEdBQVcsSUFBSSxDQUFDLGFBQWEsQ0FBQyxnQkFBZ0IsRUFBRSxDQUFDO1FBRTVELEtBQUssSUFBSSxDQUFDLEdBQUcsQ0FBQyxFQUFFLENBQUMsR0FBRyxLQUFLLEVBQUUsQ0FBQyxFQUFFLEVBQUU7WUFDOUIsSUFBSSxJQUFJLENBQUMsYUFBYSxDQUFDLGNBQWMsQ0FBQyxDQUFDLENBQUMsSUFBSSxZQUFZLEVBQUU7Z0JBQ3hELElBQU0sTUFBTSxHQUFtQixJQUFJLENBQUMsYUFBYSxDQUFDLFlBQVksQ0FBQyxDQUFDLENBQUMsQ0FBQztnQkFDbEUsT0FBTyxJQUFJLENBQUMsS0FBSyxDQUFDLE1BQU0sRUFBRSxDQUFDLEVBQUUsQ0FBQyxDQUFDLENBQUM7YUFDakM7U0FDRjtRQUVELE9BQU8sS0FBSyxDQUFDO0lBQ2YsQ0FBQztJQVFNLHNDQUFrQixHQUF6QixVQUEwQixLQUFhO1FBQXZDLGlCQXNEQztnQ0FyRFUsQ0FBQztZQUNSLElBQU0sY0FBYyxHQUFHLE9BQUssYUFBYSxDQUFDLGlCQUFpQixDQUFDLEtBQUssRUFBRSxDQUFDLENBQUMsQ0FBQztZQUd0RSxJQUFNLE1BQUksR0FBTSxLQUFLLFNBQUksQ0FBRyxDQUFDO1lBQzdCLElBQUksT0FBSyxVQUFVLEVBQUU7Z0JBQ25CLGlCQUFPLENBQUMsWUFBWSxDQUNsQix1QkFBcUIsY0FBYyxhQUFRLE1BQUksTUFBRyxDQUNuRCxDQUFDO2FBQ0g7WUFFRCxLQUFLLENBQUMsS0FBRyxPQUFLLGFBQWEsR0FBRyxjQUFnQixDQUFDO2lCQUM1QyxJQUFJLENBQUMsa0JBQVEsSUFBSSxlQUFRLENBQUMsV0FBVyxFQUFFLEVBQXRCLENBQXNCLENBQUM7aUJBQ3hDLElBQUksQ0FBQyxxQkFBVztnQkFDZixJQUFNLFNBQVMsR0FBaUIsS0FBSSxDQUFDLFVBQVUsQ0FDN0MsV0FBVyxFQUNYLFdBQVcsQ0FBQyxVQUFVLEVBQ3RCLE1BQUksQ0FDTCxDQUFDO2dCQUVGLElBQUksUUFBUSxHQUFHLEtBQUksQ0FBQyxhQUFhLENBQUMsd0JBQXdCLENBQUMsS0FBSyxFQUFFLENBQUMsQ0FBQyxDQUFDO2dCQUNyRSxJQUFJLFFBQVEsSUFBSSxHQUFHLEVBQUU7b0JBQ25CLFNBQVMsQ0FBQyxhQUFhLENBQUMsUUFBUSxDQUFDLENBQUM7aUJBQ25DO2dCQUVELFFBQVEsR0FBRyxLQUFJLENBQUMsYUFBYSxDQUFDLHlCQUF5QixDQUFDLEtBQUssRUFBRSxDQUFDLENBQUMsQ0FBQztnQkFDbEUsSUFBSSxRQUFRLElBQUksR0FBRyxFQUFFO29CQUNuQixTQUFTLENBQUMsY0FBYyxDQUFDLFFBQVEsQ0FBQyxDQUFDO2lCQUNwQztnQkFDRCxTQUFTLENBQUMsWUFBWSxDQUFDLEtBQUksQ0FBQyxZQUFZLEVBQUUsS0FBSSxDQUFDLFdBQVcsQ0FBQyxDQUFDO2dCQUU1RCxJQUFJLEtBQUksQ0FBQyxRQUFRLENBQUMsUUFBUSxDQUFDLE1BQUksQ0FBQyxJQUFJLElBQUksRUFBRTtvQkFDeEMsNkJBQWEsQ0FBQyxNQUFNLENBQUMsS0FBSSxDQUFDLFFBQVEsQ0FBQyxRQUFRLENBQUMsTUFBSSxDQUFDLENBQUMsQ0FBQztpQkFDcEQ7Z0JBRUQsS0FBSSxDQUFDLFFBQVEsQ0FBQyxRQUFRLENBQUMsTUFBSSxFQUFFLFNBQVMsQ0FBQyxDQUFDO2dCQUV4QyxLQUFJLENBQUMsWUFBWSxFQUFFLENBQUM7Z0JBQ3BCLElBQUksS0FBSSxDQUFDLFlBQVksSUFBSSxLQUFJLENBQUMsZUFBZSxFQUFFO29CQUM3QyxLQUFJLENBQUMsTUFBTSxHQUFHLFFBQVEsQ0FBQyxXQUFXLENBQUM7b0JBR25DLEtBQUksQ0FBQyxjQUFjLENBQUMsY0FBYyxFQUFFLENBQUM7b0JBRXJDLEtBQUksQ0FBQyxTQUFTLEdBQUcsS0FBSyxDQUFDO29CQUN2QixLQUFJLENBQUMsWUFBWSxHQUFHLElBQUksQ0FBQztvQkFFekIsS0FBSSxDQUFDLGNBQWMsRUFBRSxDQUFDO29CQUN0QixLQUFJLENBQUMsYUFBYSxFQUFFLENBQUM7b0JBQ3JCLEtBQUksQ0FBQyxXQUFXLEVBQUUsQ0FBQyxPQUFPLENBQUMsaUJBQUUsQ0FBQyxDQUFDO2lCQUNoQztZQUNILENBQUMsQ0FBQyxDQUFDOzs7UUFuRFAsS0FBSyxJQUFJLENBQUMsR0FBRyxDQUFDLEVBQUUsQ0FBQyxHQUFHLElBQUksQ0FBQyxhQUFhLENBQUMsY0FBYyxDQUFDLEtBQUssQ0FBQyxFQUFFLENBQUMsRUFBRTtvQkFBeEQsQ0FBQztTQW9EVDtJQUNILENBQUM7SUFLTSxrQ0FBYyxHQUFyQjtRQUNFLElBQUksQ0FBQyxRQUFRLENBQUMsS0FBSyxFQUFFLENBQUM7SUFDeEIsQ0FBQztJQUtNLHNDQUFrQixHQUF6QjtRQUNFLElBQUksQ0FBQyxZQUFZLENBQUMsS0FBSyxFQUFFLENBQUM7SUFDNUIsQ0FBQztJQUtNLDBCQUFNLEdBQWI7UUFDRSxJQUFJLElBQUksQ0FBQyxNQUFNLElBQUksSUFBSTtZQUFFLE9BQU87UUFHaEMsSUFBTSxRQUFRLEdBQWEsQ0FBQyxDQUFDLEVBQUUsQ0FBQyxFQUFFLHFCQUFNLENBQUMsS0FBSyxFQUFFLHFCQUFNLENBQUMsTUFBTSxDQUFDLENBQUM7UUFFL0QsSUFBSSxDQUFDLFdBQVcsRUFBRSxDQUFDLGNBQWMsQ0FBQywwQkFBVyxFQUFFLFFBQVEsQ0FBQyxDQUFDO1FBQ3pELElBQUksQ0FBQyxXQUFXLEVBQUUsQ0FBQyxTQUFTLEVBQUUsQ0FBQztJQUNqQyxDQUFDO0lBS00sd0JBQUksR0FBWCxVQUFZLE1BQXNCO1FBQ2hDLElBQUksSUFBSSxDQUFDLE1BQU0sSUFBSSxJQUFJLEVBQUU7WUFDdkIsT0FBTztTQUNSO1FBR0QsSUFBSSxJQUFJLENBQUMsTUFBTSxJQUFJLFFBQVEsQ0FBQyxhQUFhLEVBQUU7WUFDekMsTUFBTSxDQUFDLGdCQUFnQixDQUFDLElBQUksQ0FBQyxZQUFZLENBQUMsQ0FBQztZQUUzQyxJQUFJLENBQUMsV0FBVyxFQUFFLENBQUMsWUFBWSxDQUFDLE1BQU0sQ0FBQyxDQUFDO1lBRXhDLElBQUksQ0FBQyxNQUFNLEVBQUUsQ0FBQztTQUNmO0lBQ0gsQ0FBQztJQXNGSCxnQkFBQztBQUFELENBQUMsQ0FyMEI4QixpQ0FBZSxHQXEwQjdDO0FBcjBCWSw4QkFBUzs7Ozs7Ozs7Ozs7V0N4RXRCLG9DQUFvQywrQkFBK0IsRSIsImZpbGUiOiJtYWluLjUxYzQyMDkyMWIxY2Y4ZTdmYTYxLmhvdC11cGRhdGUuanMiLCJzb3VyY2VzQ29udGVudCI6WyIvKipcbiAqIENvcHlyaWdodChjKSBMaXZlMkQgSW5jLiBBbGwgcmlnaHRzIHJlc2VydmVkLlxuICpcbiAqIFVzZSBvZiB0aGlzIHNvdXJjZSBjb2RlIGlzIGdvdmVybmVkIGJ5IHRoZSBMaXZlMkQgT3BlbiBTb2Z0d2FyZSBsaWNlbnNlXG4gKiB0aGF0IGNhbiBiZSBmb3VuZCBhdCBodHRwczovL3d3dy5saXZlMmQuY29tL2V1bGEvbGl2ZTJkLW9wZW4tc29mdHdhcmUtbGljZW5zZS1hZ3JlZW1lbnRfZW4uaHRtbC5cbiAqL1xuXG5pbXBvcnQgJ3doYXR3Zy1mZXRjaCc7XG5cbmltcG9ydCB7IEN1YmlzbURlZmF1bHRQYXJhbWV0ZXJJZCB9IGZyb20gJ0BmcmFtZXdvcmsvY3ViaXNtZGVmYXVsdHBhcmFtZXRlcmlkJztcbmltcG9ydCB7IEN1YmlzbU1vZGVsU2V0dGluZ0pzb24gfSBmcm9tICdAZnJhbWV3b3JrL2N1YmlzbW1vZGVsc2V0dGluZ2pzb24nO1xuaW1wb3J0IHtcbiAgQnJlYXRoUGFyYW1ldGVyRGF0YSxcbiAgQ3ViaXNtQnJlYXRoXG59IGZyb20gJ0BmcmFtZXdvcmsvZWZmZWN0L2N1YmlzbWJyZWF0aCc7XG5pbXBvcnQgeyBDdWJpc21FeWVCbGluayB9IGZyb20gJ0BmcmFtZXdvcmsvZWZmZWN0L2N1YmlzbWV5ZWJsaW5rJztcbmltcG9ydCB7IElDdWJpc21Nb2RlbFNldHRpbmcgfSBmcm9tICdAZnJhbWV3b3JrL2ljdWJpc21tb2RlbHNldHRpbmcnO1xuaW1wb3J0IHsgQ3ViaXNtSWRIYW5kbGUgfSBmcm9tICdAZnJhbWV3b3JrL2lkL2N1YmlzbWlkJztcbmltcG9ydCB7IEN1YmlzbUZyYW1ld29yayB9IGZyb20gJ0BmcmFtZXdvcmsvbGl2ZTJkY3ViaXNtZnJhbWV3b3JrJztcbmltcG9ydCB7IEN1YmlzbU1hdHJpeDQ0IH0gZnJvbSAnQGZyYW1ld29yay9tYXRoL2N1YmlzbW1hdHJpeDQ0JztcbmltcG9ydCB7IEN1YmlzbVVzZXJNb2RlbCB9IGZyb20gJ0BmcmFtZXdvcmsvbW9kZWwvY3ViaXNtdXNlcm1vZGVsJztcbmltcG9ydCB7XG4gIEFDdWJpc21Nb3Rpb24sXG4gIEZpbmlzaGVkTW90aW9uQ2FsbGJhY2tcbn0gZnJvbSAnQGZyYW1ld29yay9tb3Rpb24vYWN1YmlzbW1vdGlvbic7XG5pbXBvcnQgeyBDdWJpc21Nb3Rpb24gfSBmcm9tICdAZnJhbWV3b3JrL21vdGlvbi9jdWJpc21tb3Rpb24nO1xuaW1wb3J0IHtcbiAgQ3ViaXNtTW90aW9uUXVldWVFbnRyeUhhbmRsZSxcbiAgSW52YWxpZE1vdGlvblF1ZXVlRW50cnlIYW5kbGVWYWx1ZVxufSBmcm9tICdAZnJhbWV3b3JrL21vdGlvbi9jdWJpc21tb3Rpb25xdWV1ZW1hbmFnZXInO1xuaW1wb3J0IHsgY3NtTWFwIH0gZnJvbSAnQGZyYW1ld29yay90eXBlL2NzbW1hcCc7XG5pbXBvcnQgeyBjc21SZWN0IH0gZnJvbSAnQGZyYW1ld29yay90eXBlL2NzbXJlY3RmJztcbmltcG9ydCB7IGNzbVN0cmluZyB9IGZyb20gJ0BmcmFtZXdvcmsvdHlwZS9jc21zdHJpbmcnO1xuaW1wb3J0IHsgY3NtVmVjdG9yIH0gZnJvbSAnQGZyYW1ld29yay90eXBlL2NzbXZlY3Rvcic7XG5pbXBvcnQgeyBDdWJpc21Mb2dJbmZvIH0gZnJvbSAnQGZyYW1ld29yay91dGlscy9jdWJpc21kZWJ1Zyc7XG5cbmltcG9ydCAqIGFzIExBcHBEZWZpbmUgZnJvbSAnLi9sYXBwZGVmaW5lJztcbmltcG9ydCB7IGNhbnZhcywgZnJhbWVCdWZmZXIsIGdsLCBMQXBwRGVsZWdhdGUgfSBmcm9tICcuL2xhcHBkZWxlZ2F0ZSc7XG5pbXBvcnQgeyBMQXBwUGFsIH0gZnJvbSAnLi9sYXBwcGFsJztcbmltcG9ydCB7IFRleHR1cmVJbmZvIH0gZnJvbSAnLi9sYXBwdGV4dHVyZW1hbmFnZXInO1xuaW1wb3J0IHsgTEFwcFdhdkZpbGVIYW5kbGVyIH0gZnJvbSAnLi9sYXBwd2F2ZmlsZWhhbmRsZXInO1xuXG5lbnVtIExvYWRTdGVwIHtcbiAgTG9hZEFzc2V0cyxcbiAgTG9hZE1vZGVsLFxuICBXYWl0TG9hZE1vZGVsLFxuICBMb2FkRXhwcmVzc2lvbixcbiAgV2FpdExvYWRFeHByZXNzaW9uLFxuICBMb2FkUGh5c2ljcyxcbiAgV2FpdExvYWRQaHlzaWNzLFxuICBMb2FkUG9zZSxcbiAgV2FpdExvYWRQb3NlLFxuICBTZXR1cEV5ZUJsaW5rLFxuICBTZXR1cEJyZWF0aCxcbiAgTG9hZFVzZXJEYXRhLFxuICBXYWl0TG9hZFVzZXJEYXRhLFxuICBTZXR1cEV5ZUJsaW5rSWRzLFxuICBTZXR1cExpcFN5bmNJZHMsXG4gIFNldHVwTGF5b3V0LFxuICBMb2FkTW90aW9uLFxuICBXYWl0TG9hZE1vdGlvbixcbiAgQ29tcGxldGVJbml0aWFsaXplLFxuICBDb21wbGV0ZVNldHVwTW9kZWwsXG4gIExvYWRUZXh0dXJlLFxuICBXYWl0TG9hZFRleHR1cmUsXG4gIENvbXBsZXRlU2V0dXBcbn1cblxuLyoqXG4gKiDjg6bjg7zjgrbjg7zjgYzlrp/pmpvjgavkvb/nlKjjgZnjgovjg6Ljg4fjg6vjga7lrp/oo4Xjgq/jg6njgrk8YnI+XG4gKiDjg6Ljg4fjg6vnlJ/miJDjgIHmqZ/og73jgrPjg7Pjg53jg7zjg43jg7Pjg4jnlJ/miJDjgIHmm7TmlrDlh6bnkIbjgajjg6zjg7Pjg4Djg6rjg7PjgrDjga7lkbzjgbPlh7rjgZfjgpLooYzjgYbjgIJcbiAqL1xuZXhwb3J0IGNsYXNzIExBcHBNb2RlbCBleHRlbmRzIEN1YmlzbVVzZXJNb2RlbCB7XG4gIC8qKlxuICAgKiBtb2RlbDMuanNvbuOBjOe9ruOBi+OCjOOBn+ODh+OCo+ODrOOCr+ODiOODquOBqOODleOCoeOCpOODq+ODkeOCueOBi+OCieODouODh+ODq+OCkueUn+aIkOOBmeOCi1xuICAgKiBAcGFyYW0gZGlyXG4gICAqIEBwYXJhbSBmaWxlTmFtZVxuICAgKi9cbiAgcHVibGljIGxvYWRBc3NldHMoZGlyOiBzdHJpbmcsIGZpbGVOYW1lOiBzdHJpbmcpOiB2b2lkIHtcbiAgICB0aGlzLl9tb2RlbEhvbWVEaXIgPSBkaXI7XG5cbiAgICBmZXRjaChgJHt0aGlzLl9tb2RlbEhvbWVEaXJ9JHtmaWxlTmFtZX1gKVxuICAgICAgLnRoZW4ocmVzcG9uc2UgPT4gcmVzcG9uc2UuYXJyYXlCdWZmZXIoKSlcbiAgICAgIC50aGVuKGFycmF5QnVmZmVyID0+IHtcbiAgICAgICAgY29uc3Qgc2V0dGluZzogSUN1YmlzbU1vZGVsU2V0dGluZyA9IG5ldyBDdWJpc21Nb2RlbFNldHRpbmdKc29uKFxuICAgICAgICAgIGFycmF5QnVmZmVyLFxuICAgICAgICAgIGFycmF5QnVmZmVyLmJ5dGVMZW5ndGhcbiAgICAgICAgKTtcblxuICAgICAgICAvLyDjgrnjg4bjg7zjg4jjgpLmm7TmlrBcbiAgICAgICAgdGhpcy5fc3RhdGUgPSBMb2FkU3RlcC5Mb2FkTW9kZWw7XG5cbiAgICAgICAgLy8g57WQ5p6c44KS5L+d5a2YXG4gICAgICAgIHRoaXMuc2V0dXBNb2RlbChzZXR0aW5nKTtcbiAgICAgIH0pO1xuICB9XG5cbiAgLyoqXG4gICAqIG1vZGVsMy5qc29u44GL44KJ44Oi44OH44Or44KS55Sf5oiQ44GZ44KL44CCXG4gICAqIG1vZGVsMy5qc29u44Gu6KiY6L+w44Gr5b6T44Gj44Gm44Oi44OH44Or55Sf5oiQ44CB44Oi44O844K344On44Oz44CB54mp55CG5ryU566X44Gq44Gp44Gu44Kz44Oz44Od44O844ON44Oz44OI55Sf5oiQ44KS6KGM44GG44CCXG4gICAqXG4gICAqIEBwYXJhbSBzZXR0aW5nIElDdWJpc21Nb2RlbFNldHRpbmfjga7jgqTjg7Pjgrnjgr/jg7PjgrlcbiAgICovXG4gIHByaXZhdGUgc2V0dXBNb2RlbChzZXR0aW5nOiBJQ3ViaXNtTW9kZWxTZXR0aW5nKTogdm9pZCB7XG4gICAgdGhpcy5fdXBkYXRpbmcgPSB0cnVlO1xuICAgIHRoaXMuX2luaXRpYWxpemVkID0gZmFsc2U7XG5cbiAgICB0aGlzLl9tb2RlbFNldHRpbmcgPSBzZXR0aW5nO1xuXG4gICAgLy8gQ3ViaXNtTW9kZWxcbiAgICBpZiAodGhpcy5fbW9kZWxTZXR0aW5nLmdldE1vZGVsRmlsZU5hbWUoKSAhPSAnJykge1xuICAgICAgY29uc3QgbW9kZWxGaWxlTmFtZSA9IHRoaXMuX21vZGVsU2V0dGluZy5nZXRNb2RlbEZpbGVOYW1lKCk7XG5cbiAgICAgIGZldGNoKGAke3RoaXMuX21vZGVsSG9tZURpcn0ke21vZGVsRmlsZU5hbWV9YClcbiAgICAgICAgLnRoZW4ocmVzcG9uc2UgPT4gcmVzcG9uc2UuYXJyYXlCdWZmZXIoKSlcbiAgICAgICAgLnRoZW4oYXJyYXlCdWZmZXIgPT4ge1xuICAgICAgICAgIHRoaXMubG9hZE1vZGVsKGFycmF5QnVmZmVyKTtcbiAgICAgICAgICB0aGlzLl9zdGF0ZSA9IExvYWRTdGVwLkxvYWRFeHByZXNzaW9uO1xuXG4gICAgICAgICAgLy8gY2FsbGJhY2tcbiAgICAgICAgICBsb2FkQ3ViaXNtRXhwcmVzc2lvbigpO1xuICAgICAgICB9KTtcblxuICAgICAgdGhpcy5fc3RhdGUgPSBMb2FkU3RlcC5XYWl0TG9hZE1vZGVsO1xuICAgIH0gZWxzZSB7XG4gICAgICBMQXBwUGFsLnByaW50TWVzc2FnZSgnTW9kZWwgZGF0YSBkb2VzIG5vdCBleGlzdC4nKTtcbiAgICB9XG5cbiAgICAvLyBFeHByZXNzaW9uXG4gICAgY29uc3QgbG9hZEN1YmlzbUV4cHJlc3Npb24gPSAoKTogdm9pZCA9PiB7XG4gICAgICBpZiAodGhpcy5fbW9kZWxTZXR0aW5nLmdldEV4cHJlc3Npb25Db3VudCgpID4gMCkge1xuICAgICAgICBjb25zdCBjb3VudDogbnVtYmVyID0gdGhpcy5fbW9kZWxTZXR0aW5nLmdldEV4cHJlc3Npb25Db3VudCgpO1xuXG4gICAgICAgIGZvciAobGV0IGkgPSAwOyBpIDwgY291bnQ7IGkrKykge1xuICAgICAgICAgIGNvbnN0IGV4cHJlc3Npb25OYW1lID0gdGhpcy5fbW9kZWxTZXR0aW5nLmdldEV4cHJlc3Npb25OYW1lKGkpO1xuICAgICAgICAgIGNvbnN0IGV4cHJlc3Npb25GaWxlTmFtZSA9IHRoaXMuX21vZGVsU2V0dGluZy5nZXRFeHByZXNzaW9uRmlsZU5hbWUoXG4gICAgICAgICAgICBpXG4gICAgICAgICAgKTtcblxuICAgICAgICAgIGZldGNoKGAke3RoaXMuX21vZGVsSG9tZURpcn0ke2V4cHJlc3Npb25GaWxlTmFtZX1gKVxuICAgICAgICAgICAgLnRoZW4ocmVzcG9uc2UgPT4gcmVzcG9uc2UuYXJyYXlCdWZmZXIoKSlcbiAgICAgICAgICAgIC50aGVuKGFycmF5QnVmZmVyID0+IHtcbiAgICAgICAgICAgICAgY29uc3QgbW90aW9uOiBBQ3ViaXNtTW90aW9uID0gdGhpcy5sb2FkRXhwcmVzc2lvbihcbiAgICAgICAgICAgICAgICBhcnJheUJ1ZmZlcixcbiAgICAgICAgICAgICAgICBhcnJheUJ1ZmZlci5ieXRlTGVuZ3RoLFxuICAgICAgICAgICAgICAgIGV4cHJlc3Npb25OYW1lXG4gICAgICAgICAgICAgICk7XG5cbiAgICAgICAgICAgICAgaWYgKHRoaXMuX2V4cHJlc3Npb25zLmdldFZhbHVlKGV4cHJlc3Npb25OYW1lKSAhPSBudWxsKSB7XG4gICAgICAgICAgICAgICAgQUN1YmlzbU1vdGlvbi5kZWxldGUoXG4gICAgICAgICAgICAgICAgICB0aGlzLl9leHByZXNzaW9ucy5nZXRWYWx1ZShleHByZXNzaW9uTmFtZSlcbiAgICAgICAgICAgICAgICApO1xuICAgICAgICAgICAgICAgIHRoaXMuX2V4cHJlc3Npb25zLnNldFZhbHVlKGV4cHJlc3Npb25OYW1lLCBudWxsKTtcbiAgICAgICAgICAgICAgfVxuXG4gICAgICAgICAgICAgIHRoaXMuX2V4cHJlc3Npb25zLnNldFZhbHVlKGV4cHJlc3Npb25OYW1lLCBtb3Rpb24pO1xuXG4gICAgICAgICAgICAgIHRoaXMuX2V4cHJlc3Npb25Db3VudCsrO1xuXG4gICAgICAgICAgICAgIGlmICh0aGlzLl9leHByZXNzaW9uQ291bnQgPj0gY291bnQpIHtcbiAgICAgICAgICAgICAgICB0aGlzLl9zdGF0ZSA9IExvYWRTdGVwLkxvYWRQaHlzaWNzO1xuXG4gICAgICAgICAgICAgICAgLy8gY2FsbGJhY2tcbiAgICAgICAgICAgICAgICBsb2FkQ3ViaXNtUGh5c2ljcygpO1xuICAgICAgICAgICAgICB9XG4gICAgICAgICAgICB9KTtcbiAgICAgICAgfVxuICAgICAgICB0aGlzLl9zdGF0ZSA9IExvYWRTdGVwLldhaXRMb2FkRXhwcmVzc2lvbjtcbiAgICAgIH0gZWxzZSB7XG4gICAgICAgIHRoaXMuX3N0YXRlID0gTG9hZFN0ZXAuTG9hZFBoeXNpY3M7XG5cbiAgICAgICAgLy8gY2FsbGJhY2tcbiAgICAgICAgbG9hZEN1YmlzbVBoeXNpY3MoKTtcbiAgICAgIH1cbiAgICB9O1xuXG4gICAgLy8gUGh5c2ljc1xuICAgIGNvbnN0IGxvYWRDdWJpc21QaHlzaWNzID0gKCk6IHZvaWQgPT4ge1xuICAgICAgaWYgKHRoaXMuX21vZGVsU2V0dGluZy5nZXRQaHlzaWNzRmlsZU5hbWUoKSAhPSAnJykge1xuICAgICAgICBjb25zdCBwaHlzaWNzRmlsZU5hbWUgPSB0aGlzLl9tb2RlbFNldHRpbmcuZ2V0UGh5c2ljc0ZpbGVOYW1lKCk7XG5cbiAgICAgICAgZmV0Y2goYCR7dGhpcy5fbW9kZWxIb21lRGlyfSR7cGh5c2ljc0ZpbGVOYW1lfWApXG4gICAgICAgICAgLnRoZW4ocmVzcG9uc2UgPT4gcmVzcG9uc2UuYXJyYXlCdWZmZXIoKSlcbiAgICAgICAgICAudGhlbihhcnJheUJ1ZmZlciA9PiB7XG4gICAgICAgICAgICB0aGlzLmxvYWRQaHlzaWNzKGFycmF5QnVmZmVyLCBhcnJheUJ1ZmZlci5ieXRlTGVuZ3RoKTtcblxuICAgICAgICAgICAgdGhpcy5fc3RhdGUgPSBMb2FkU3RlcC5Mb2FkUG9zZTtcblxuICAgICAgICAgICAgLy8gY2FsbGJhY2tcbiAgICAgICAgICAgIGxvYWRDdWJpc21Qb3NlKCk7XG4gICAgICAgICAgfSk7XG4gICAgICAgIHRoaXMuX3N0YXRlID0gTG9hZFN0ZXAuV2FpdExvYWRQaHlzaWNzO1xuICAgICAgfSBlbHNlIHtcbiAgICAgICAgdGhpcy5fc3RhdGUgPSBMb2FkU3RlcC5Mb2FkUG9zZTtcblxuICAgICAgICAvLyBjYWxsYmFja1xuICAgICAgICBsb2FkQ3ViaXNtUG9zZSgpO1xuICAgICAgfVxuICAgIH07XG5cbiAgICAvLyBQb3NlXG4gICAgY29uc3QgbG9hZEN1YmlzbVBvc2UgPSAoKTogdm9pZCA9PiB7XG4gICAgICBpZiAodGhpcy5fbW9kZWxTZXR0aW5nLmdldFBvc2VGaWxlTmFtZSgpICE9ICcnKSB7XG4gICAgICAgIGNvbnN0IHBvc2VGaWxlTmFtZSA9IHRoaXMuX21vZGVsU2V0dGluZy5nZXRQb3NlRmlsZU5hbWUoKTtcblxuICAgICAgICBmZXRjaChgJHt0aGlzLl9tb2RlbEhvbWVEaXJ9JHtwb3NlRmlsZU5hbWV9YClcbiAgICAgICAgICAudGhlbihyZXNwb25zZSA9PiByZXNwb25zZS5hcnJheUJ1ZmZlcigpKVxuICAgICAgICAgIC50aGVuKGFycmF5QnVmZmVyID0+IHtcbiAgICAgICAgICAgIHRoaXMubG9hZFBvc2UoYXJyYXlCdWZmZXIsIGFycmF5QnVmZmVyLmJ5dGVMZW5ndGgpO1xuXG4gICAgICAgICAgICB0aGlzLl9zdGF0ZSA9IExvYWRTdGVwLlNldHVwRXllQmxpbms7XG5cbiAgICAgICAgICAgIC8vIGNhbGxiYWNrXG4gICAgICAgICAgICBzZXR1cEV5ZUJsaW5rKCk7XG4gICAgICAgICAgfSk7XG4gICAgICAgIHRoaXMuX3N0YXRlID0gTG9hZFN0ZXAuV2FpdExvYWRQb3NlO1xuICAgICAgfSBlbHNlIHtcbiAgICAgICAgdGhpcy5fc3RhdGUgPSBMb2FkU3RlcC5TZXR1cEV5ZUJsaW5rO1xuXG4gICAgICAgIC8vIGNhbGxiYWNrXG4gICAgICAgIHNldHVwRXllQmxpbmsoKTtcbiAgICAgIH1cbiAgICB9O1xuXG4gICAgLy8gRXllQmxpbmtcbiAgICBjb25zdCBzZXR1cEV5ZUJsaW5rID0gKCk6IHZvaWQgPT4ge1xuICAgICAgLy8gaWYgKHRoaXMuX21vZGVsU2V0dGluZy5nZXRFeWVCbGlua1BhcmFtZXRlckNvdW50KCkgPiAwKSB7XG4gICAgICAvLyAgIHRoaXMuX2V5ZUJsaW5rID0gQ3ViaXNtRXllQmxpbmsuY3JlYXRlKHRoaXMuX21vZGVsU2V0dGluZyk7XG4gICAgICAvLyAgIHRoaXMuX3N0YXRlID0gTG9hZFN0ZXAuU2V0dXBCcmVhdGg7XG4gICAgICAvLyB9XG5cbiAgICAgIC8vIGNhbGxiYWNrXG4gICAgICBzZXR1cEJyZWF0aCgpO1xuICAgIH07XG5cbiAgICAvLyBCcmVhdGhcbiAgICBjb25zdCBzZXR1cEJyZWF0aCA9ICgpOiB2b2lkID0+IHtcbiAgICAgIHRoaXMuX2JyZWF0aCA9IEN1YmlzbUJyZWF0aC5jcmVhdGUoKTtcblxuICAgICAgY29uc3QgYnJlYXRoUGFyYW1ldGVyczogY3NtVmVjdG9yPEJyZWF0aFBhcmFtZXRlckRhdGE+ID0gbmV3IGNzbVZlY3RvcigpO1xuICAgICAgLy8gYnJlYXRoUGFyYW1ldGVycy5wdXNoQmFjayhcbiAgICAgIC8vICAgbmV3IEJyZWF0aFBhcmFtZXRlckRhdGEodGhpcy5faWRQYXJhbUFuZ2xlWCwgMC4wLCAxNS4wLCA2LjUzNDUsIDAuNSlcbiAgICAgIC8vICk7XG4gICAgICAvLyBicmVhdGhQYXJhbWV0ZXJzLnB1c2hCYWNrKFxuICAgICAgLy8gICBuZXcgQnJlYXRoUGFyYW1ldGVyRGF0YSh0aGlzLl9pZFBhcmFtQW5nbGVZLCAwLjAsIDguMCwgMy41MzQ1LCAwLjUpXG4gICAgICAvLyApO1xuICAgICAgLy8gYnJlYXRoUGFyYW1ldGVycy5wdXNoQmFjayhcbiAgICAgIC8vICAgbmV3IEJyZWF0aFBhcmFtZXRlckRhdGEodGhpcy5faWRQYXJhbUFuZ2xlWiwgMC4wLCAxMC4wLCA1LjUzNDUsIDAuNSlcbiAgICAgIC8vICk7XG4gICAgICAvLyBicmVhdGhQYXJhbWV0ZXJzLnB1c2hCYWNrKFxuICAgICAgLy8gICBuZXcgQnJlYXRoUGFyYW1ldGVyRGF0YSh0aGlzLl9pZFBhcmFtQm9keUFuZ2xlWCwgMC4wLCA0LjAsIDE1LjUzNDUsIDAuNSlcbiAgICAgIC8vICk7XG4gICAgICBicmVhdGhQYXJhbWV0ZXJzLnB1c2hCYWNrKFxuICAgICAgICBuZXcgQnJlYXRoUGFyYW1ldGVyRGF0YShcbiAgICAgICAgICBDdWJpc21GcmFtZXdvcmsuZ2V0SWRNYW5hZ2VyKCkuZ2V0SWQoXG4gICAgICAgICAgICBDdWJpc21EZWZhdWx0UGFyYW1ldGVySWQuUGFyYW1CcmVhdGhcbiAgICAgICAgICApLFxuICAgICAgICAgIDAuMCxcbiAgICAgICAgICAwLjUsXG4gICAgICAgICAgMy4yMzQ1LFxuICAgICAgICAgIDAuNVxuICAgICAgICApXG4gICAgICApO1xuXG4gICAgICB0aGlzLl9icmVhdGguc2V0UGFyYW1ldGVycyhicmVhdGhQYXJhbWV0ZXJzKTtcbiAgICAgIHRoaXMuX3N0YXRlID0gTG9hZFN0ZXAuTG9hZFVzZXJEYXRhO1xuXG4gICAgICAvLyBjYWxsYmFja1xuICAgICAgbG9hZFVzZXJEYXRhKCk7XG4gICAgfTtcblxuICAgIC8vIFVzZXJEYXRhXG4gICAgY29uc3QgbG9hZFVzZXJEYXRhID0gKCk6IHZvaWQgPT4ge1xuICAgICAgaWYgKHRoaXMuX21vZGVsU2V0dGluZy5nZXRVc2VyRGF0YUZpbGUoKSAhPSAnJykge1xuICAgICAgICBjb25zdCB1c2VyRGF0YUZpbGUgPSB0aGlzLl9tb2RlbFNldHRpbmcuZ2V0VXNlckRhdGFGaWxlKCk7XG5cbiAgICAgICAgZmV0Y2goYCR7dGhpcy5fbW9kZWxIb21lRGlyfSR7dXNlckRhdGFGaWxlfWApXG4gICAgICAgICAgLnRoZW4ocmVzcG9uc2UgPT4gcmVzcG9uc2UuYXJyYXlCdWZmZXIoKSlcbiAgICAgICAgICAudGhlbihhcnJheUJ1ZmZlciA9PiB7XG4gICAgICAgICAgICB0aGlzLmxvYWRVc2VyRGF0YShhcnJheUJ1ZmZlciwgYXJyYXlCdWZmZXIuYnl0ZUxlbmd0aCk7XG5cbiAgICAgICAgICAgIHRoaXMuX3N0YXRlID0gTG9hZFN0ZXAuU2V0dXBFeWVCbGlua0lkcztcblxuICAgICAgICAgICAgLy8gY2FsbGJhY2tcbiAgICAgICAgICAgIHNldHVwRXllQmxpbmtJZHMoKTtcbiAgICAgICAgICB9KTtcblxuICAgICAgICB0aGlzLl9zdGF0ZSA9IExvYWRTdGVwLldhaXRMb2FkVXNlckRhdGE7XG4gICAgICB9IGVsc2Uge1xuICAgICAgICB0aGlzLl9zdGF0ZSA9IExvYWRTdGVwLlNldHVwRXllQmxpbmtJZHM7XG5cbiAgICAgICAgLy8gY2FsbGJhY2tcbiAgICAgICAgc2V0dXBFeWVCbGlua0lkcygpO1xuICAgICAgfVxuICAgIH07XG5cbiAgICAvLyBFeWVCbGlua0lkc1xuICAgIGNvbnN0IHNldHVwRXllQmxpbmtJZHMgPSAoKTogdm9pZCA9PiB7XG4gICAgICBjb25zdCBleWVCbGlua0lkQ291bnQ6IG51bWJlciA9IHRoaXMuX21vZGVsU2V0dGluZy5nZXRFeWVCbGlua1BhcmFtZXRlckNvdW50KCk7XG5cbiAgICAgIGZvciAobGV0IGkgPSAwOyBpIDwgZXllQmxpbmtJZENvdW50OyArK2kpIHtcbiAgICAgICAgdGhpcy5fZXllQmxpbmtJZHMucHVzaEJhY2soXG4gICAgICAgICAgdGhpcy5fbW9kZWxTZXR0aW5nLmdldEV5ZUJsaW5rUGFyYW1ldGVySWQoaSlcbiAgICAgICAgKTtcbiAgICAgIH1cblxuICAgICAgdGhpcy5fc3RhdGUgPSBMb2FkU3RlcC5TZXR1cExpcFN5bmNJZHM7XG5cbiAgICAgIC8vIGNhbGxiYWNrXG4gICAgICBzZXR1cExpcFN5bmNJZHMoKTtcbiAgICB9O1xuXG4gICAgLy8gTGlwU3luY0lkc1xuICAgIGNvbnN0IHNldHVwTGlwU3luY0lkcyA9ICgpOiB2b2lkID0+IHtcbiAgICAgIGNvbnN0IGxpcFN5bmNJZENvdW50ID0gdGhpcy5fbW9kZWxTZXR0aW5nLmdldExpcFN5bmNQYXJhbWV0ZXJDb3VudCgpO1xuXG4gICAgICBmb3IgKGxldCBpID0gMDsgaSA8IGxpcFN5bmNJZENvdW50OyArK2kpIHtcbiAgICAgICAgdGhpcy5fbGlwU3luY0lkcy5wdXNoQmFjayh0aGlzLl9tb2RlbFNldHRpbmcuZ2V0TGlwU3luY1BhcmFtZXRlcklkKGkpKTtcbiAgICAgIH1cbiAgICAgIHRoaXMuX3N0YXRlID0gTG9hZFN0ZXAuU2V0dXBMYXlvdXQ7XG5cbiAgICAgIC8vIGNhbGxiYWNrXG4gICAgICBzZXR1cExheW91dCgpO1xuICAgIH07XG5cbiAgICAvLyBMYXlvdXRcbiAgICBjb25zdCBzZXR1cExheW91dCA9ICgpOiB2b2lkID0+IHtcbiAgICAgIGNvbnN0IGxheW91dDogY3NtTWFwPHN0cmluZywgbnVtYmVyPiA9IG5ldyBjc21NYXA8c3RyaW5nLCBudW1iZXI+KCk7XG4gICAgICB0aGlzLl9tb2RlbFNldHRpbmcuZ2V0TGF5b3V0TWFwKGxheW91dCk7XG4gICAgICB0aGlzLl9tb2RlbE1hdHJpeC5zZXR1cEZyb21MYXlvdXQobGF5b3V0KTtcbiAgICAgIHRoaXMuX3N0YXRlID0gTG9hZFN0ZXAuTG9hZE1vdGlvbjtcblxuICAgICAgLy8gY2FsbGJhY2tcbiAgICAgIGxvYWRDdWJpc21Nb3Rpb24oKTtcbiAgICB9O1xuXG4gICAgLy8gTW90aW9uXG4gICAgY29uc3QgbG9hZEN1YmlzbU1vdGlvbiA9ICgpOiB2b2lkID0+IHtcbiAgICAgIHRoaXMuX3N0YXRlID0gTG9hZFN0ZXAuV2FpdExvYWRNb3Rpb247XG4gICAgICB0aGlzLl9tb2RlbC5zYXZlUGFyYW1ldGVycygpO1xuICAgICAgdGhpcy5fYWxsTW90aW9uQ291bnQgPSAwO1xuICAgICAgdGhpcy5fbW90aW9uQ291bnQgPSAwO1xuICAgICAgY29uc3QgZ3JvdXA6IHN0cmluZ1tdID0gW107XG5cbiAgICAgIGNvbnN0IG1vdGlvbkdyb3VwQ291bnQ6IG51bWJlciA9IHRoaXMuX21vZGVsU2V0dGluZy5nZXRNb3Rpb25Hcm91cENvdW50KCk7XG5cbiAgICAgIC8vIOODouODvOOCt+ODp+ODs+OBrue3j+aVsOOCkuaxguOCgeOCi1xuICAgICAgZm9yIChsZXQgaSA9IDA7IGkgPCBtb3Rpb25Hcm91cENvdW50OyBpKyspIHtcbiAgICAgICAgZ3JvdXBbaV0gPSB0aGlzLl9tb2RlbFNldHRpbmcuZ2V0TW90aW9uR3JvdXBOYW1lKGkpO1xuICAgICAgICB0aGlzLl9hbGxNb3Rpb25Db3VudCArPSB0aGlzLl9tb2RlbFNldHRpbmcuZ2V0TW90aW9uQ291bnQoZ3JvdXBbaV0pO1xuICAgICAgfVxuXG4gICAgICAvLyDjg6Ljg7zjgrfjg6fjg7Pjga7oqq3jgb/ovrzjgb9cbiAgICAgIGZvciAobGV0IGkgPSAwOyBpIDwgbW90aW9uR3JvdXBDb3VudDsgaSsrKSB7XG4gICAgICAgIHRoaXMucHJlTG9hZE1vdGlvbkdyb3VwKGdyb3VwW2ldKTtcbiAgICAgIH1cblxuICAgICAgLy8g44Oi44O844K344On44Oz44GM44Gq44GE5aC05ZCIXG4gICAgICBpZiAobW90aW9uR3JvdXBDb3VudCA9PSAwKSB7XG4gICAgICAgIHRoaXMuX3N0YXRlID0gTG9hZFN0ZXAuTG9hZFRleHR1cmU7XG5cbiAgICAgICAgLy8g5YWo44Gm44Gu44Oi44O844K344On44Oz44KS5YGc5q2i44GZ44KLXG4gICAgICAgIHRoaXMuX21vdGlvbk1hbmFnZXIuc3RvcEFsbE1vdGlvbnMoKTtcblxuICAgICAgICB0aGlzLl91cGRhdGluZyA9IGZhbHNlO1xuICAgICAgICB0aGlzLl9pbml0aWFsaXplZCA9IHRydWU7XG5cbiAgICAgICAgdGhpcy5jcmVhdGVSZW5kZXJlcigpO1xuICAgICAgICB0aGlzLnNldHVwVGV4dHVyZXMoKTtcbiAgICAgICAgdGhpcy5nZXRSZW5kZXJlcigpLnN0YXJ0VXAoZ2wpO1xuICAgICAgfVxuICAgIH07XG4gIH1cblxuICAvKipcbiAgICog44OG44Kv44K544OB44Oj44Om44OL44OD44OI44Gr44OG44Kv44K544OB44Oj44KS44Ot44O844OJ44GZ44KLXG4gICAqL1xuICBwcml2YXRlIHNldHVwVGV4dHVyZXMoKTogdm9pZCB7XG4gICAgLy8gaVBob25l44Gn44Gu44Ki44Or44OV44Kh5ZOB6LOq5ZCR5LiK44Gu44Gf44KBVHlwZXNjcmlwdOOBp+OBr3ByZW11bHRpcGxpZWRBbHBoYeOCkuaOoeeUqFxuICAgIGNvbnN0IHVzZVByZW11bHRpcGx5ID0gdHJ1ZTtcblxuICAgIGlmICh0aGlzLl9zdGF0ZSA9PSBMb2FkU3RlcC5Mb2FkVGV4dHVyZSkge1xuICAgICAgLy8g44OG44Kv44K544OB44Oj6Kqt44G/6L6844G/55SoXG4gICAgICBjb25zdCB0ZXh0dXJlQ291bnQ6IG51bWJlciA9IHRoaXMuX21vZGVsU2V0dGluZy5nZXRUZXh0dXJlQ291bnQoKTtcblxuICAgICAgZm9yIChcbiAgICAgICAgbGV0IG1vZGVsVGV4dHVyZU51bWJlciA9IDA7XG4gICAgICAgIG1vZGVsVGV4dHVyZU51bWJlciA8IHRleHR1cmVDb3VudDtcbiAgICAgICAgbW9kZWxUZXh0dXJlTnVtYmVyKytcbiAgICAgICkge1xuICAgICAgICAvLyDjg4bjgq/jgrnjg4Hjg6PlkI3jgYznqbrmloflrZfjgaDjgaPjgZ/loLTlkIjjga/jg63jg7zjg4njg7vjg5DjgqTjg7Pjg4nlh6bnkIbjgpLjgrnjgq3jg4Pjg5dcbiAgICAgICAgaWYgKHRoaXMuX21vZGVsU2V0dGluZy5nZXRUZXh0dXJlRmlsZU5hbWUobW9kZWxUZXh0dXJlTnVtYmVyKSA9PSAnJykge1xuICAgICAgICAgIGNvbnNvbGUubG9nKCdnZXRUZXh0dXJlRmlsZU5hbWUgbnVsbCcpO1xuICAgICAgICAgIGNvbnRpbnVlO1xuICAgICAgICB9XG5cbiAgICAgICAgLy8gV2ViR0zjga7jg4bjgq/jgrnjg4Hjg6Pjg6bjg4vjg4Pjg4jjgavjg4bjgq/jgrnjg4Hjg6PjgpLjg63jg7zjg4njgZnjgotcbiAgICAgICAgbGV0IHRleHR1cmVQYXRoID0gdGhpcy5fbW9kZWxTZXR0aW5nLmdldFRleHR1cmVGaWxlTmFtZShcbiAgICAgICAgICBtb2RlbFRleHR1cmVOdW1iZXJcbiAgICAgICAgKTtcbiAgICAgICAgdGV4dHVyZVBhdGggPSB0aGlzLl9tb2RlbEhvbWVEaXIgKyB0ZXh0dXJlUGF0aDtcblxuICAgICAgICAvLyDjg63jg7zjg4nlrozkuobmmYLjgavlkbzjgbPlh7rjgZnjgrPjg7zjg6vjg5Djg4Pjgq/plqLmlbBcbiAgICAgICAgY29uc3Qgb25Mb2FkID0gKHRleHR1cmVJbmZvOiBUZXh0dXJlSW5mbyk6IHZvaWQgPT4ge1xuICAgICAgICAgIHRoaXMuZ2V0UmVuZGVyZXIoKS5iaW5kVGV4dHVyZShtb2RlbFRleHR1cmVOdW1iZXIsIHRleHR1cmVJbmZvLmlkKTtcblxuICAgICAgICAgIHRoaXMuX3RleHR1cmVDb3VudCsrO1xuXG4gICAgICAgICAgaWYgKHRoaXMuX3RleHR1cmVDb3VudCA+PSB0ZXh0dXJlQ291bnQpIHtcbiAgICAgICAgICAgIC8vIOODreODvOODieWujOS6hlxuICAgICAgICAgICAgdGhpcy5fc3RhdGUgPSBMb2FkU3RlcC5Db21wbGV0ZVNldHVwO1xuICAgICAgICAgIH1cbiAgICAgICAgfTtcblxuICAgICAgICAvLyDoqq3jgb/ovrzjgb9cbiAgICAgICAgTEFwcERlbGVnYXRlLmdldEluc3RhbmNlKClcbiAgICAgICAgICAuZ2V0VGV4dHVyZU1hbmFnZXIoKVxuICAgICAgICAgIC5jcmVhdGVUZXh0dXJlRnJvbVBuZ0ZpbGUodGV4dHVyZVBhdGgsIHVzZVByZW11bHRpcGx5LCBvbkxvYWQpO1xuICAgICAgICB0aGlzLmdldFJlbmRlcmVyKCkuc2V0SXNQcmVtdWx0aXBsaWVkQWxwaGEodXNlUHJlbXVsdGlwbHkpO1xuICAgICAgfVxuXG4gICAgICB0aGlzLl9zdGF0ZSA9IExvYWRTdGVwLldhaXRMb2FkVGV4dHVyZTtcbiAgICB9XG4gIH1cblxuICAvKipcbiAgICog44Os44Oz44OA44Op44KS5YaN5qeL56+J44GZ44KLXG4gICAqL1xuICBwdWJsaWMgcmVsb2FkUmVuZGVyZXIoKTogdm9pZCB7XG4gICAgdGhpcy5kZWxldGVSZW5kZXJlcigpO1xuICAgIHRoaXMuY3JlYXRlUmVuZGVyZXIoKTtcbiAgICB0aGlzLnNldHVwVGV4dHVyZXMoKTtcbiAgfVxuXG4gIC8qKlxuICAgKiDmm7TmlrBcbiAgICovXG4gIHB1YmxpYyB1cGRhdGUoKTogdm9pZCB7XG4gICAgaWYgKHRoaXMuX3N0YXRlICE9IExvYWRTdGVwLkNvbXBsZXRlU2V0dXApIHJldHVybjtcblxuICAgIGNvbnN0IGRlbHRhVGltZVNlY29uZHM6IG51bWJlciA9IExBcHBQYWwuZ2V0RGVsdGFUaW1lKCk7XG4gICAgdGhpcy5fdXNlclRpbWVTZWNvbmRzICs9IGRlbHRhVGltZVNlY29uZHM7XG5cbiAgICB0aGlzLl9kcmFnTWFuYWdlci51cGRhdGUoZGVsdGFUaW1lU2Vjb25kcyk7XG4gICAgdGhpcy5fZHJhZ1ggPSB0aGlzLl9kcmFnTWFuYWdlci5nZXRYKCk7XG4gICAgdGhpcy5fZHJhZ1kgPSB0aGlzLl9kcmFnTWFuYWdlci5nZXRZKCk7XG5cbiAgICAvLyDjg6Ljg7zjgrfjg6fjg7Pjgavjgojjgovjg5Hjg6njg6Hjg7zjgr/mm7TmlrDjga7mnInnhKFcbiAgICBsZXQgbW90aW9uVXBkYXRlZCA9IGZhbHNlO1xuXG4gICAgLy8tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLVxuICAgIHRoaXMuX21vZGVsLmxvYWRQYXJhbWV0ZXJzKCk7IC8vIOWJjeWbnuOCu+ODvOODluOBleOCjOOBn+eKtuaFi+OCkuODreODvOODiVxuICAgIC8vIGlmICh0aGlzLl9tb3Rpb25NYW5hZ2VyLmlzRmluaXNoZWQoKSkge1xuICAgIC8vICAgLy8g44Oi44O844K344On44Oz44Gu5YaN55Sf44GM44Gq44GE5aC05ZCI44CB5b6F5qmf44Oi44O844K344On44Oz44Gu5Lit44GL44KJ44Op44Oz44OA44Og44Gn5YaN55Sf44GZ44KLXG4gICAgLy8gICB0aGlzLnN0YXJ0UmFuZG9tTW90aW9uKFxuICAgIC8vICAgICBMQXBwRGVmaW5lLk1vdGlvbkdyb3VwSWRsZSxcbiAgICAvLyAgICAgTEFwcERlZmluZS5Qcmlvcml0eUlkbGVcbiAgICAvLyAgICk7XG4gICAgLy8gfSBlbHNlIHtcbiAgICAvLyAgIG1vdGlvblVwZGF0ZWQgPSB0aGlzLl9tb3Rpb25NYW5hZ2VyLnVwZGF0ZU1vdGlvbihcbiAgICAvLyAgICAgdGhpcy5fbW9kZWwsXG4gICAgLy8gICAgIGRlbHRhVGltZVNlY29uZHNcbiAgICAvLyAgICk7IC8vIOODouODvOOCt+ODp+ODs+OCkuabtOaWsFxuICAgIC8vIH1cbiAgICAvLyB0aGlzLl9tb2RlbC5zYXZlUGFyYW1ldGVycygpOyAvLyDnirbmhYvjgpLkv53lrZhcbiAgICAvLy0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tXG5cbiAgICAvLyDjgb7jgbDjgZ/jgY1cbiAgICBpZiAoIW1vdGlvblVwZGF0ZWQpIHtcbiAgICAgIGlmICh0aGlzLl9leWVCbGluayAhPSBudWxsKSB7XG4gICAgICAgIC8vIOODoeOCpOODs+ODouODvOOCt+ODp+ODs+OBruabtOaWsOOBjOOBquOBhOOBqOOBjVxuICAgICAgICB0aGlzLl9leWVCbGluay51cGRhdGVQYXJhbWV0ZXJzKHRoaXMuX21vZGVsLCBkZWx0YVRpbWVTZWNvbmRzKTsgLy8g55uu44OR44OBXG4gICAgICB9XG4gICAgfVxuXG4gICAgaWYgKHRoaXMuX2V4cHJlc3Npb25NYW5hZ2VyICE9IG51bGwpIHtcbiAgICAgIHRoaXMuX2V4cHJlc3Npb25NYW5hZ2VyLnVwZGF0ZU1vdGlvbih0aGlzLl9tb2RlbCwgZGVsdGFUaW1lU2Vjb25kcyk7IC8vIOihqOaDheOBp+ODkeODqeODoeODvOOCv+abtOaWsO+8iOebuOWvvuWkieWMlu+8iVxuICAgIH1cblxuICAgIC8vIHRoaXMgc3R1ZmYgaXMgdG8gc3VwcG9ydCB0aGUgdG91Y2ggY29udHJvbHMgb2YgdGhlIGJhc2UgYXBwLlxuICAgIC8vIC8vIOODieODqeODg+OCsOOBq+OCiOOCi+WkieWMllxuICAgIC8vIC8vIOODieODqeODg+OCsOOBq+OCiOOCi+mhlOOBruWQkeOBjeOBruiqv+aVtFxuICAgIC8vIHRoaXMuX21vZGVsLmFkZFBhcmFtZXRlclZhbHVlQnlJZCh0aGlzLl9pZFBhcmFtQW5nbGVYLCB0aGlzLl9kcmFnWCAqIDMwKTsgLy8gLTMw44GL44KJMzDjga7lgKTjgpLliqDjgYjjgotcbiAgICAvLyB0aGlzLl9tb2RlbC5hZGRQYXJhbWV0ZXJWYWx1ZUJ5SWQodGhpcy5faWRQYXJhbUFuZ2xlWSwgdGhpcy5fZHJhZ1kgKiAzMCk7XG4gICAgLy8gdGhpcy5fbW9kZWwuYWRkUGFyYW1ldGVyVmFsdWVCeUlkKFxuICAgIC8vICAgdGhpcy5faWRQYXJhbUFuZ2xlWixcbiAgICAvLyAgIHRoaXMuX2RyYWdYICogdGhpcy5fZHJhZ1kgKiAtMzBcbiAgICAvLyApO1xuXG4gICAgLy8gLy8g44OJ44Op44OD44Kw44Gr44KI44KL5L2T44Gu5ZCR44GN44Gu6Kq/5pW0XG4gICAgLy8gdGhpcy5fbW9kZWwuYWRkUGFyYW1ldGVyVmFsdWVCeUlkKFxuICAgIC8vICAgdGhpcy5faWRQYXJhbUJvZHlBbmdsZVgsXG4gICAgLy8gICB0aGlzLl9kcmFnWCAqIDEwXG4gICAgLy8gKTsgLy8gLTEw44GL44KJMTDjga7lgKTjgpLliqDjgYjjgotcblxuICAgIC8vIC8vIOODieODqeODg+OCsOOBq+OCiOOCi+ebruOBruWQkeOBjeOBruiqv+aVtFxuICAgIC8vIHRoaXMuX21vZGVsLmFkZFBhcmFtZXRlclZhbHVlQnlJZCh0aGlzLl9pZFBhcmFtRXllQmFsbFgsIHRoaXMuX2RyYWdYKTsgLy8gLTHjgYvjgokx44Gu5YCk44KS5Yqg44GI44KLXG4gICAgLy8gdGhpcy5fbW9kZWwuYWRkUGFyYW1ldGVyVmFsdWVCeUlkKHRoaXMuX2lkUGFyYW1FeWVCYWxsWSwgdGhpcy5fZHJhZ1kpO1xuXG4gICAgbGV0IGhvc3RQYXJhbXM6QXJyYXk8U3RyaW5nPiA9IE9iamVjdC5rZXlzKHRoaXMuX2FwcEhvc3RQYXJhbXMpXG4gICAgZm9yKFxuICAgICAgbGV0IGkgPSAwLCBwYXJhbTtcbiAgICAgIHBhcmFtID0gaG9zdFBhcmFtc1tpXTtcbiAgICAgIGkrKywgcGFyYW0uc3RhcnRzV2l0aChcIlBhcmFtXCIpICYmIHRoaXMuX21vZGVsLmFkZFBhcmFtZXRlclZhbHVlQnlJZChcbiAgICAgICAgQ3ViaXNtRnJhbWV3b3JrLmdldElkTWFuYWdlcigpLmdldElkKHBhcmFtKSxcbiAgICAgICAgdGhpcy5fYXBwSG9zdFBhcmFtc1twYXJhbV1cbiAgICAgIClcbiAgICApO1xuXG4gICAgLy8g5ZG85ZC444Gq44GpXG4gICAgaWYgKHRoaXMuX2JyZWF0aCAhPSBudWxsKSB7XG4gICAgICB0aGlzLl9icmVhdGgudXBkYXRlUGFyYW1ldGVycyh0aGlzLl9tb2RlbCwgZGVsdGFUaW1lU2Vjb25kcyk7XG4gICAgfVxuXG4gICAgLy8g54mp55CG5ryU566X44Gu6Kit5a6aXG4gICAgaWYgKHRoaXMuX3BoeXNpY3MgIT0gbnVsbCkge1xuICAgICAgdGhpcy5fcGh5c2ljcy5ldmFsdWF0ZSh0aGlzLl9tb2RlbCwgZGVsdGFUaW1lU2Vjb25kcyk7XG4gICAgfVxuXG4gICAgLy8g44Oq44OD44OX44K344Oz44Kv44Gu6Kit5a6aXG4gICAgaWYgKHRoaXMuX2xpcHN5bmMpIHtcbiAgICAgIGxldCB2YWx1ZSA9IDAuMDsgLy8g44Oq44Ki44Or44K/44Kk44Og44Gn44Oq44OD44OX44K344Oz44Kv44KS6KGM44GG5aC05ZCI44CB44K344K544OG44Og44GL44KJ6Z+z6YeP44KS5Y+W5b6X44GX44Gm44CBMH4x44Gu56+E5Zuy44Gn5YCk44KS5YWl5Yqb44GX44G+44GZ44CCXG5cbiAgICAgIHRoaXMuX3dhdkZpbGVIYW5kbGVyLnVwZGF0ZShkZWx0YVRpbWVTZWNvbmRzKTtcbiAgICAgIHZhbHVlID0gdGhpcy5fd2F2RmlsZUhhbmRsZXIuZ2V0Um1zKCk7XG5cbiAgICAgIGZvciAobGV0IGkgPSAwOyBpIDwgdGhpcy5fbGlwU3luY0lkcy5nZXRTaXplKCk7ICsraSkge1xuICAgICAgICB0aGlzLl9tb2RlbC5hZGRQYXJhbWV0ZXJWYWx1ZUJ5SWQodGhpcy5fbGlwU3luY0lkcy5hdChpKSwgdmFsdWUsIDAuOCk7XG4gICAgICB9XG4gICAgfVxuXG4gICAgLy8g44Od44O844K644Gu6Kit5a6aXG4gICAgaWYgKHRoaXMuX3Bvc2UgIT0gbnVsbCkge1xuICAgICAgdGhpcy5fcG9zZS51cGRhdGVQYXJhbWV0ZXJzKHRoaXMuX21vZGVsLCBkZWx0YVRpbWVTZWNvbmRzKTtcbiAgICB9XG5cbiAgICB0aGlzLl9tb2RlbC51cGRhdGUoKTtcbiAgfVxuXG4gIC8qKlxuICAgKiDlvJXmlbDjgafmjIflrprjgZfjgZ/jg6Ljg7zjgrfjg6fjg7Pjga7lho3nlJ/jgpLplovlp4vjgZnjgotcbiAgICogQHBhcmFtIGdyb3VwIOODouODvOOCt+ODp+ODs+OCsOODq+ODvOODl+WQjVxuICAgKiBAcGFyYW0gbm8g44Kw44Or44O844OX5YaF44Gu55Wq5Y+3XG4gICAqIEBwYXJhbSBwcmlvcml0eSDlhKrlhYjluqZcbiAgICogQHBhcmFtIG9uRmluaXNoZWRNb3Rpb25IYW5kbGVyIOODouODvOOCt+ODp+ODs+WGjeeUn+e1guS6huaZguOBq+WRvOOBs+WHuuOBleOCjOOCi+OCs+ODvOODq+ODkOODg+OCr+mWouaVsFxuICAgKiBAcmV0dXJuIOmWi+Wni+OBl+OBn+ODouODvOOCt+ODp+ODs+OBruitmOWIpeeVquWPt+OCkui/lOOBmeOAguWAi+WIpeOBruODouODvOOCt+ODp+ODs+OBjOe1guS6huOBl+OBn+OBi+WQpuOBi+OCkuWIpOWumuOBmeOCi2lzRmluaXNoZWQoKeOBruW8leaVsOOBp+S9v+eUqOOBmeOCi+OAgumWi+Wni+OBp+OBjeOBquOBhOaZguOBr1stMV1cbiAgICovXG4gIHB1YmxpYyBzdGFydE1vdGlvbihcbiAgICBncm91cDogc3RyaW5nLFxuICAgIG5vOiBudW1iZXIsXG4gICAgcHJpb3JpdHk6IG51bWJlcixcbiAgICBvbkZpbmlzaGVkTW90aW9uSGFuZGxlcj86IEZpbmlzaGVkTW90aW9uQ2FsbGJhY2tcbiAgKTogQ3ViaXNtTW90aW9uUXVldWVFbnRyeUhhbmRsZSB7XG4gICAgaWYgKHByaW9yaXR5ID09IExBcHBEZWZpbmUuUHJpb3JpdHlGb3JjZSkge1xuICAgICAgdGhpcy5fbW90aW9uTWFuYWdlci5zZXRSZXNlcnZlUHJpb3JpdHkocHJpb3JpdHkpO1xuICAgIH0gZWxzZSBpZiAoIXRoaXMuX21vdGlvbk1hbmFnZXIucmVzZXJ2ZU1vdGlvbihwcmlvcml0eSkpIHtcbiAgICAgIGlmICh0aGlzLl9kZWJ1Z01vZGUpIHtcbiAgICAgICAgTEFwcFBhbC5wcmludE1lc3NhZ2UoXCJbQVBQXWNhbid0IHN0YXJ0IG1vdGlvbi5cIik7XG4gICAgICB9XG4gICAgICByZXR1cm4gSW52YWxpZE1vdGlvblF1ZXVlRW50cnlIYW5kbGVWYWx1ZTtcbiAgICB9XG5cbiAgICBjb25zdCBtb3Rpb25GaWxlTmFtZSA9IHRoaXMuX21vZGVsU2V0dGluZy5nZXRNb3Rpb25GaWxlTmFtZShncm91cCwgbm8pO1xuXG4gICAgLy8gZXgpIGlkbGVfMFxuICAgIGNvbnN0IG5hbWUgPSBgJHtncm91cH1fJHtub31gO1xuICAgIGxldCBtb3Rpb246IEN1YmlzbU1vdGlvbiA9IHRoaXMuX21vdGlvbnMuZ2V0VmFsdWUobmFtZSkgYXMgQ3ViaXNtTW90aW9uO1xuICAgIGxldCBhdXRvRGVsZXRlID0gZmFsc2U7XG5cbiAgICBpZiAobW90aW9uID09IG51bGwpIHtcbiAgICAgIGZldGNoKGAke3RoaXMuX21vZGVsSG9tZURpcn0ke21vdGlvbkZpbGVOYW1lfWApXG4gICAgICAgIC50aGVuKHJlc3BvbnNlID0+IHJlc3BvbnNlLmFycmF5QnVmZmVyKCkpXG4gICAgICAgIC50aGVuKGFycmF5QnVmZmVyID0+IHtcbiAgICAgICAgICBtb3Rpb24gPSB0aGlzLmxvYWRNb3Rpb24oXG4gICAgICAgICAgICBhcnJheUJ1ZmZlcixcbiAgICAgICAgICAgIGFycmF5QnVmZmVyLmJ5dGVMZW5ndGgsXG4gICAgICAgICAgICBudWxsLFxuICAgICAgICAgICAgb25GaW5pc2hlZE1vdGlvbkhhbmRsZXJcbiAgICAgICAgICApO1xuICAgICAgICAgIGxldCBmYWRlVGltZTogbnVtYmVyID0gdGhpcy5fbW9kZWxTZXR0aW5nLmdldE1vdGlvbkZhZGVJblRpbWVWYWx1ZShcbiAgICAgICAgICAgIGdyb3VwLFxuICAgICAgICAgICAgbm9cbiAgICAgICAgICApO1xuXG4gICAgICAgICAgaWYgKGZhZGVUaW1lID49IDAuMCkge1xuICAgICAgICAgICAgbW90aW9uLnNldEZhZGVJblRpbWUoZmFkZVRpbWUpO1xuICAgICAgICAgIH1cblxuICAgICAgICAgIGZhZGVUaW1lID0gdGhpcy5fbW9kZWxTZXR0aW5nLmdldE1vdGlvbkZhZGVPdXRUaW1lVmFsdWUoZ3JvdXAsIG5vKTtcbiAgICAgICAgICBpZiAoZmFkZVRpbWUgPj0gMC4wKSB7XG4gICAgICAgICAgICBtb3Rpb24uc2V0RmFkZU91dFRpbWUoZmFkZVRpbWUpO1xuICAgICAgICAgIH1cblxuICAgICAgICAgIG1vdGlvbi5zZXRFZmZlY3RJZHModGhpcy5fZXllQmxpbmtJZHMsIHRoaXMuX2xpcFN5bmNJZHMpO1xuICAgICAgICAgIGF1dG9EZWxldGUgPSB0cnVlOyAvLyDntYLkuobmmYLjgavjg6Hjg6Ljg6rjgYvjgonliYrpmaRcbiAgICAgICAgfSk7XG4gICAgfSBlbHNlIHtcbiAgICAgIG1vdGlvbi5zZXRGaW5pc2hlZE1vdGlvbkhhbmRsZXIob25GaW5pc2hlZE1vdGlvbkhhbmRsZXIpO1xuICAgIH1cblxuICAgIC8vdm9pY2VcbiAgICBjb25zdCB2b2ljZSA9IHRoaXMuX21vZGVsU2V0dGluZy5nZXRNb3Rpb25Tb3VuZEZpbGVOYW1lKGdyb3VwLCBubyk7XG4gICAgaWYgKHZvaWNlLmxvY2FsZUNvbXBhcmUoJycpICE9IDApIHtcbiAgICAgIGxldCBwYXRoID0gdm9pY2U7XG4gICAgICBwYXRoID0gdGhpcy5fbW9kZWxIb21lRGlyICsgcGF0aDtcbiAgICAgIHRoaXMuX3dhdkZpbGVIYW5kbGVyLnN0YXJ0KHBhdGgpO1xuICAgIH1cblxuICAgIGlmICh0aGlzLl9kZWJ1Z01vZGUpIHtcbiAgICAgIExBcHBQYWwucHJpbnRNZXNzYWdlKGBbQVBQXXN0YXJ0IG1vdGlvbjogWyR7Z3JvdXB9XyR7bm99YCk7XG4gICAgfVxuICAgIHJldHVybiB0aGlzLl9tb3Rpb25NYW5hZ2VyLnN0YXJ0TW90aW9uUHJpb3JpdHkoXG4gICAgICBtb3Rpb24sXG4gICAgICBhdXRvRGVsZXRlLFxuICAgICAgcHJpb3JpdHlcbiAgICApO1xuICB9XG5cbiAgLyoqXG4gICAqIOODqeODs+ODgOODoOOBq+mBuOOBsOOCjOOBn+ODouODvOOCt+ODp+ODs+OBruWGjeeUn+OCkumWi+Wni+OBmeOCi+OAglxuICAgKiBAcGFyYW0gZ3JvdXAg44Oi44O844K344On44Oz44Kw44Or44O844OX5ZCNXG4gICAqIEBwYXJhbSBwcmlvcml0eSDlhKrlhYjluqZcbiAgICogQHBhcmFtIG9uRmluaXNoZWRNb3Rpb25IYW5kbGVyIOODouODvOOCt+ODp+ODs+WGjeeUn+e1guS6huaZguOBq+WRvOOBs+WHuuOBleOCjOOCi+OCs+ODvOODq+ODkOODg+OCr+mWouaVsFxuICAgKiBAcmV0dXJuIOmWi+Wni+OBl+OBn+ODouODvOOCt+ODp+ODs+OBruitmOWIpeeVquWPt+OCkui/lOOBmeOAguWAi+WIpeOBruODouODvOOCt+ODp+ODs+OBjOe1guS6huOBl+OBn+OBi+WQpuOBi+OCkuWIpOWumuOBmeOCi2lzRmluaXNoZWQoKeOBruW8leaVsOOBp+S9v+eUqOOBmeOCi+OAgumWi+Wni+OBp+OBjeOBquOBhOaZguOBr1stMV1cbiAgICovXG4gIHB1YmxpYyBzdGFydFJhbmRvbU1vdGlvbihcbiAgICBncm91cDogc3RyaW5nLFxuICAgIHByaW9yaXR5OiBudW1iZXIsXG4gICAgb25GaW5pc2hlZE1vdGlvbkhhbmRsZXI/OiBGaW5pc2hlZE1vdGlvbkNhbGxiYWNrXG4gICk6IEN1YmlzbU1vdGlvblF1ZXVlRW50cnlIYW5kbGUge1xuICAgIGlmICh0aGlzLl9tb2RlbFNldHRpbmcuZ2V0TW90aW9uQ291bnQoZ3JvdXApID09IDApIHtcbiAgICAgIHJldHVybiBJbnZhbGlkTW90aW9uUXVldWVFbnRyeUhhbmRsZVZhbHVlO1xuICAgIH1cblxuICAgIGNvbnN0IG5vOiBudW1iZXIgPSBNYXRoLmZsb29yKFxuICAgICAgTWF0aC5yYW5kb20oKSAqIHRoaXMuX21vZGVsU2V0dGluZy5nZXRNb3Rpb25Db3VudChncm91cClcbiAgICApO1xuXG4gICAgcmV0dXJuIHRoaXMuc3RhcnRNb3Rpb24oZ3JvdXAsIG5vLCBwcmlvcml0eSwgb25GaW5pc2hlZE1vdGlvbkhhbmRsZXIpO1xuICB9XG5cbiAgLyoqXG4gICAqIOW8leaVsOOBp+aMh+WumuOBl+OBn+ihqOaDheODouODvOOCt+ODp+ODs+OCkuOCu+ODg+ODiOOBmeOCi1xuICAgKlxuICAgKiBAcGFyYW0gZXhwcmVzc2lvbklkIOihqOaDheODouODvOOCt+ODp+ODs+OBrklEXG4gICAqL1xuICBwdWJsaWMgc2V0RXhwcmVzc2lvbihleHByZXNzaW9uSWQ6IHN0cmluZyk6IHZvaWQge1xuICAgIGNvbnN0IG1vdGlvbjogQUN1YmlzbU1vdGlvbiA9IHRoaXMuX2V4cHJlc3Npb25zLmdldFZhbHVlKGV4cHJlc3Npb25JZCk7XG5cbiAgICBpZiAodGhpcy5fZGVidWdNb2RlKSB7XG4gICAgICBMQXBwUGFsLnByaW50TWVzc2FnZShgW0FQUF1leHByZXNzaW9uOiBbJHtleHByZXNzaW9uSWR9XWApO1xuICAgIH1cblxuICAgIGlmIChtb3Rpb24gIT0gbnVsbCkge1xuICAgICAgdGhpcy5fZXhwcmVzc2lvbk1hbmFnZXIuc3RhcnRNb3Rpb25Qcmlvcml0eShcbiAgICAgICAgbW90aW9uLFxuICAgICAgICBmYWxzZSxcbiAgICAgICAgTEFwcERlZmluZS5Qcmlvcml0eUZvcmNlXG4gICAgICApO1xuICAgIH0gZWxzZSB7XG4gICAgICBpZiAodGhpcy5fZGVidWdNb2RlKSB7XG4gICAgICAgIExBcHBQYWwucHJpbnRNZXNzYWdlKGBbQVBQXWV4cHJlc3Npb25bJHtleHByZXNzaW9uSWR9XSBpcyBudWxsYCk7XG4gICAgICB9XG4gICAgfVxuICB9XG5cbiAgLyoqXG4gICAqIOODqeODs+ODgOODoOOBq+mBuOOBsOOCjOOBn+ihqOaDheODouODvOOCt+ODp+ODs+OCkuOCu+ODg+ODiOOBmeOCi1xuICAgKi9cbiAgcHVibGljIHNldFJhbmRvbUV4cHJlc3Npb24oKTogdm9pZCB7XG4gICAgaWYgKHRoaXMuX2V4cHJlc3Npb25zLmdldFNpemUoKSA9PSAwKSB7XG4gICAgICByZXR1cm47XG4gICAgfVxuXG4gICAgY29uc3Qgbm86IG51bWJlciA9IE1hdGguZmxvb3IoTWF0aC5yYW5kb20oKSAqIHRoaXMuX2V4cHJlc3Npb25zLmdldFNpemUoKSk7XG5cbiAgICBmb3IgKGxldCBpID0gMDsgaSA8IHRoaXMuX2V4cHJlc3Npb25zLmdldFNpemUoKTsgaSsrKSB7XG4gICAgICBpZiAoaSA9PSBubykge1xuICAgICAgICBjb25zdCBuYW1lOiBzdHJpbmcgPSB0aGlzLl9leHByZXNzaW9ucy5fa2V5VmFsdWVzW2ldLmZpcnN0O1xuICAgICAgICB0aGlzLnNldEV4cHJlc3Npb24obmFtZSk7XG4gICAgICAgIHJldHVybjtcbiAgICAgIH1cbiAgICB9XG4gIH1cblxuICAvKipcbiAgICog44Kk44OZ44Oz44OI44Gu55m654Gr44KS5Y+X44GR5Y+W44KLXG4gICAqL1xuICBwdWJsaWMgbW90aW9uRXZlbnRGaXJlZChldmVudFZhbHVlOiBjc21TdHJpbmcpOiB2b2lkIHtcbiAgICBDdWJpc21Mb2dJbmZvKCd7MH0gaXMgZmlyZWQgb24gTEFwcE1vZGVsISEnLCBldmVudFZhbHVlLnMpO1xuICB9XG5cbiAgLyoqXG4gICAqIOW9k+OBn+OCiuWIpOWumuODhuOCueODiFxuICAgKiDmjIflrprvvKnvvKTjga7poILngrnjg6rjgrnjg4jjgYvjgonnn6nlvaLjgpLoqIjnrpfjgZfjgIHluqfmqJnjgpLjgYznn6nlvaLnr4Tlm7LlhoXjgYvliKTlrprjgZnjgovjgIJcbiAgICpcbiAgICogQHBhcmFtIGhpdEFyZW5hTmFtZSAg5b2T44Gf44KK5Yik5a6a44KS44OG44K544OI44GZ44KL5a++6LGh44GuSURcbiAgICogQHBhcmFtIHggICAgICAgICAgICAg5Yik5a6a44KS6KGM44GGWOW6p+aomVxuICAgKiBAcGFyYW0geSAgICAgICAgICAgICDliKTlrprjgpLooYzjgYZZ5bqn5qiZXG4gICAqL1xuICBwdWJsaWMgaGl0VGVzdChoaXRBcmVuYU5hbWU6IHN0cmluZywgeDogbnVtYmVyLCB5OiBudW1iZXIpOiBib29sZWFuIHtcbiAgICAvLyDpgI/mmI7mmYLjga/lvZPjgZ/jgorliKTlrprnhKHjgZfjgIJcbiAgICBpZiAodGhpcy5fb3BhY2l0eSA8IDEpIHtcbiAgICAgIHJldHVybiBmYWxzZTtcbiAgICB9XG5cbiAgICBjb25zdCBjb3VudDogbnVtYmVyID0gdGhpcy5fbW9kZWxTZXR0aW5nLmdldEhpdEFyZWFzQ291bnQoKTtcblxuICAgIGZvciAobGV0IGkgPSAwOyBpIDwgY291bnQ7IGkrKykge1xuICAgICAgaWYgKHRoaXMuX21vZGVsU2V0dGluZy5nZXRIaXRBcmVhTmFtZShpKSA9PSBoaXRBcmVuYU5hbWUpIHtcbiAgICAgICAgY29uc3QgZHJhd0lkOiBDdWJpc21JZEhhbmRsZSA9IHRoaXMuX21vZGVsU2V0dGluZy5nZXRIaXRBcmVhSWQoaSk7XG4gICAgICAgIHJldHVybiB0aGlzLmlzSGl0KGRyYXdJZCwgeCwgeSk7XG4gICAgICB9XG4gICAgfVxuXG4gICAgcmV0dXJuIGZhbHNlO1xuICB9XG5cbiAgLyoqXG4gICAqIOODouODvOOCt+ODp+ODs+ODh+ODvOOCv+OCkuOCsOODq+ODvOODl+WQjeOBi+OCieS4gOaLrOOBp+ODreODvOODieOBmeOCi+OAglxuICAgKiDjg6Ljg7zjgrfjg6fjg7Pjg4fjg7zjgr/jga7lkI3liY3jga/lhoXpg6jjgadNb2RlbFNldHRpbmfjgYvjgonlj5blvpfjgZnjgovjgIJcbiAgICpcbiAgICogQHBhcmFtIGdyb3VwIOODouODvOOCt+ODp+ODs+ODh+ODvOOCv+OBruOCsOODq+ODvOODl+WQjVxuICAgKi9cbiAgcHVibGljIHByZUxvYWRNb3Rpb25Hcm91cChncm91cDogc3RyaW5nKTogdm9pZCB7XG4gICAgZm9yIChsZXQgaSA9IDA7IGkgPCB0aGlzLl9tb2RlbFNldHRpbmcuZ2V0TW90aW9uQ291bnQoZ3JvdXApOyBpKyspIHtcbiAgICAgIGNvbnN0IG1vdGlvbkZpbGVOYW1lID0gdGhpcy5fbW9kZWxTZXR0aW5nLmdldE1vdGlvbkZpbGVOYW1lKGdyb3VwLCBpKTtcblxuICAgICAgLy8gZXgpIGlkbGVfMFxuICAgICAgY29uc3QgbmFtZSA9IGAke2dyb3VwfV8ke2l9YDtcbiAgICAgIGlmICh0aGlzLl9kZWJ1Z01vZGUpIHtcbiAgICAgICAgTEFwcFBhbC5wcmludE1lc3NhZ2UoXG4gICAgICAgICAgYFtBUFBdbG9hZCBtb3Rpb246ICR7bW90aW9uRmlsZU5hbWV9ID0+IFske25hbWV9XWBcbiAgICAgICAgKTtcbiAgICAgIH1cblxuICAgICAgZmV0Y2goYCR7dGhpcy5fbW9kZWxIb21lRGlyfSR7bW90aW9uRmlsZU5hbWV9YClcbiAgICAgICAgLnRoZW4ocmVzcG9uc2UgPT4gcmVzcG9uc2UuYXJyYXlCdWZmZXIoKSlcbiAgICAgICAgLnRoZW4oYXJyYXlCdWZmZXIgPT4ge1xuICAgICAgICAgIGNvbnN0IHRtcE1vdGlvbjogQ3ViaXNtTW90aW9uID0gdGhpcy5sb2FkTW90aW9uKFxuICAgICAgICAgICAgYXJyYXlCdWZmZXIsXG4gICAgICAgICAgICBhcnJheUJ1ZmZlci5ieXRlTGVuZ3RoLFxuICAgICAgICAgICAgbmFtZVxuICAgICAgICAgICk7XG5cbiAgICAgICAgICBsZXQgZmFkZVRpbWUgPSB0aGlzLl9tb2RlbFNldHRpbmcuZ2V0TW90aW9uRmFkZUluVGltZVZhbHVlKGdyb3VwLCBpKTtcbiAgICAgICAgICBpZiAoZmFkZVRpbWUgPj0gMC4wKSB7XG4gICAgICAgICAgICB0bXBNb3Rpb24uc2V0RmFkZUluVGltZShmYWRlVGltZSk7XG4gICAgICAgICAgfVxuXG4gICAgICAgICAgZmFkZVRpbWUgPSB0aGlzLl9tb2RlbFNldHRpbmcuZ2V0TW90aW9uRmFkZU91dFRpbWVWYWx1ZShncm91cCwgaSk7XG4gICAgICAgICAgaWYgKGZhZGVUaW1lID49IDAuMCkge1xuICAgICAgICAgICAgdG1wTW90aW9uLnNldEZhZGVPdXRUaW1lKGZhZGVUaW1lKTtcbiAgICAgICAgICB9XG4gICAgICAgICAgdG1wTW90aW9uLnNldEVmZmVjdElkcyh0aGlzLl9leWVCbGlua0lkcywgdGhpcy5fbGlwU3luY0lkcyk7XG5cbiAgICAgICAgICBpZiAodGhpcy5fbW90aW9ucy5nZXRWYWx1ZShuYW1lKSAhPSBudWxsKSB7XG4gICAgICAgICAgICBBQ3ViaXNtTW90aW9uLmRlbGV0ZSh0aGlzLl9tb3Rpb25zLmdldFZhbHVlKG5hbWUpKTtcbiAgICAgICAgICB9XG5cbiAgICAgICAgICB0aGlzLl9tb3Rpb25zLnNldFZhbHVlKG5hbWUsIHRtcE1vdGlvbik7XG5cbiAgICAgICAgICB0aGlzLl9tb3Rpb25Db3VudCsrO1xuICAgICAgICAgIGlmICh0aGlzLl9tb3Rpb25Db3VudCA+PSB0aGlzLl9hbGxNb3Rpb25Db3VudCkge1xuICAgICAgICAgICAgdGhpcy5fc3RhdGUgPSBMb2FkU3RlcC5Mb2FkVGV4dHVyZTtcblxuICAgICAgICAgICAgLy8g5YWo44Gm44Gu44Oi44O844K344On44Oz44KS5YGc5q2i44GZ44KLXG4gICAgICAgICAgICB0aGlzLl9tb3Rpb25NYW5hZ2VyLnN0b3BBbGxNb3Rpb25zKCk7XG5cbiAgICAgICAgICAgIHRoaXMuX3VwZGF0aW5nID0gZmFsc2U7XG4gICAgICAgICAgICB0aGlzLl9pbml0aWFsaXplZCA9IHRydWU7XG5cbiAgICAgICAgICAgIHRoaXMuY3JlYXRlUmVuZGVyZXIoKTtcbiAgICAgICAgICAgIHRoaXMuc2V0dXBUZXh0dXJlcygpO1xuICAgICAgICAgICAgdGhpcy5nZXRSZW5kZXJlcigpLnN0YXJ0VXAoZ2wpO1xuICAgICAgICAgIH1cbiAgICAgICAgfSk7XG4gICAgfVxuICB9XG5cbiAgLyoqXG4gICAqIOOBmeOBueOBpuOBruODouODvOOCt+ODp+ODs+ODh+ODvOOCv+OCkuino+aUvuOBmeOCi+OAglxuICAgKi9cbiAgcHVibGljIHJlbGVhc2VNb3Rpb25zKCk6IHZvaWQge1xuICAgIHRoaXMuX21vdGlvbnMuY2xlYXIoKTtcbiAgfVxuXG4gIC8qKlxuICAgKiDlhajjgabjga7ooajmg4Xjg4fjg7zjgr/jgpLop6PmlL7jgZnjgovjgIJcbiAgICovXG4gIHB1YmxpYyByZWxlYXNlRXhwcmVzc2lvbnMoKTogdm9pZCB7XG4gICAgdGhpcy5fZXhwcmVzc2lvbnMuY2xlYXIoKTtcbiAgfVxuXG4gIC8qKlxuICAgKiDjg6Ljg4fjg6vjgpLmj4/nlLvjgZnjgovlh6bnkIbjgILjg6Ljg4fjg6vjgpLmj4/nlLvjgZnjgovnqbrplpPjga5WaWV3LVByb2plY3Rpb27ooYzliJfjgpLmuKHjgZnjgIJcbiAgICovXG4gIHB1YmxpYyBkb0RyYXcoKTogdm9pZCB7XG4gICAgaWYgKHRoaXMuX21vZGVsID09IG51bGwpIHJldHVybjtcblxuICAgIC8vIOOCreODo+ODs+ODkOOCueOCteOCpOOCuuOCkua4oeOBmVxuICAgIGNvbnN0IHZpZXdwb3J0OiBudW1iZXJbXSA9IFswLCAwLCBjYW52YXMud2lkdGgsIGNhbnZhcy5oZWlnaHRdO1xuXG4gICAgdGhpcy5nZXRSZW5kZXJlcigpLnNldFJlbmRlclN0YXRlKGZyYW1lQnVmZmVyLCB2aWV3cG9ydCk7XG4gICAgdGhpcy5nZXRSZW5kZXJlcigpLmRyYXdNb2RlbCgpO1xuICB9XG5cbiAgLyoqXG4gICAqIOODouODh+ODq+OCkuaPj+eUu+OBmeOCi+WHpueQhuOAguODouODh+ODq+OCkuaPj+eUu+OBmeOCi+epuumWk+OBrlZpZXctUHJvamVjdGlvbuihjOWIl+OCkua4oeOBmeOAglxuICAgKi9cbiAgcHVibGljIGRyYXcobWF0cml4OiBDdWJpc21NYXRyaXg0NCk6IHZvaWQge1xuICAgIGlmICh0aGlzLl9tb2RlbCA9PSBudWxsKSB7XG4gICAgICByZXR1cm47XG4gICAgfVxuXG4gICAgLy8g5ZCE6Kqt44G/6L6844G/57WC5LqG5b6MXG4gICAgaWYgKHRoaXMuX3N0YXRlID09IExvYWRTdGVwLkNvbXBsZXRlU2V0dXApIHtcbiAgICAgIG1hdHJpeC5tdWx0aXBseUJ5TWF0cml4KHRoaXMuX21vZGVsTWF0cml4KTtcblxuICAgICAgdGhpcy5nZXRSZW5kZXJlcigpLnNldE12cE1hdHJpeChtYXRyaXgpO1xuXG4gICAgICB0aGlzLmRvRHJhdygpO1xuICAgIH1cbiAgfVxuXG4gIC8qKlxuICAgKiDjgrPjg7Pjgrnjg4jjg6njgq/jgr9cbiAgICovXG4gIHB1YmxpYyBjb25zdHJ1Y3RvcigpIHtcbiAgICBzdXBlcigpO1xuXG4gICAgdGhpcy5fbW9kZWxTZXR0aW5nID0gbnVsbDtcbiAgICB0aGlzLl9tb2RlbEhvbWVEaXIgPSBudWxsO1xuICAgIHRoaXMuX3VzZXJUaW1lU2Vjb25kcyA9IDAuMDtcblxuICAgIHRoaXMuX2V5ZUJsaW5rSWRzID0gbmV3IGNzbVZlY3RvcjxDdWJpc21JZEhhbmRsZT4oKTtcbiAgICB0aGlzLl9saXBTeW5jSWRzID0gbmV3IGNzbVZlY3RvcjxDdWJpc21JZEhhbmRsZT4oKTtcblxuICAgIHRoaXMuX21vdGlvbnMgPSBuZXcgY3NtTWFwPHN0cmluZywgQUN1YmlzbU1vdGlvbj4oKTtcbiAgICB0aGlzLl9leHByZXNzaW9ucyA9IG5ldyBjc21NYXA8c3RyaW5nLCBBQ3ViaXNtTW90aW9uPigpO1xuXG4gICAgdGhpcy5faGl0QXJlYSA9IG5ldyBjc21WZWN0b3I8Y3NtUmVjdD4oKTtcbiAgICB0aGlzLl91c2VyQXJlYSA9IG5ldyBjc21WZWN0b3I8Y3NtUmVjdD4oKTtcblxuICAgIHRoaXMuX2lkUGFyYW1BbmdsZVggPSBDdWJpc21GcmFtZXdvcmsuZ2V0SWRNYW5hZ2VyKCkuZ2V0SWQoXG4gICAgICBDdWJpc21EZWZhdWx0UGFyYW1ldGVySWQuUGFyYW1BbmdsZVhcbiAgICApO1xuICAgIHRoaXMuX2lkUGFyYW1BbmdsZVkgPSBDdWJpc21GcmFtZXdvcmsuZ2V0SWRNYW5hZ2VyKCkuZ2V0SWQoXG4gICAgICBDdWJpc21EZWZhdWx0UGFyYW1ldGVySWQuUGFyYW1BbmdsZVlcbiAgICApO1xuICAgIHRoaXMuX2lkUGFyYW1BbmdsZVogPSBDdWJpc21GcmFtZXdvcmsuZ2V0SWRNYW5hZ2VyKCkuZ2V0SWQoXG4gICAgICBDdWJpc21EZWZhdWx0UGFyYW1ldGVySWQuUGFyYW1BbmdsZVpcbiAgICApO1xuICAgIHRoaXMuX2lkUGFyYW1FeWVCYWxsWCA9IEN1YmlzbUZyYW1ld29yay5nZXRJZE1hbmFnZXIoKS5nZXRJZChcbiAgICAgIEN1YmlzbURlZmF1bHRQYXJhbWV0ZXJJZC5QYXJhbUV5ZUJhbGxYXG4gICAgKTtcbiAgICB0aGlzLl9pZFBhcmFtRXllQmFsbFkgPSBDdWJpc21GcmFtZXdvcmsuZ2V0SWRNYW5hZ2VyKCkuZ2V0SWQoXG4gICAgICBDdWJpc21EZWZhdWx0UGFyYW1ldGVySWQuUGFyYW1FeWVCYWxsWVxuICAgICk7XG4gICAgdGhpcy5faWRQYXJhbUJvZHlBbmdsZVggPSBDdWJpc21GcmFtZXdvcmsuZ2V0SWRNYW5hZ2VyKCkuZ2V0SWQoXG4gICAgICBDdWJpc21EZWZhdWx0UGFyYW1ldGVySWQuUGFyYW1Cb2R5QW5nbGVYXG4gICAgKTtcblxuICAgIHRoaXMuX3N0YXRlID0gTG9hZFN0ZXAuTG9hZEFzc2V0cztcbiAgICB0aGlzLl9leHByZXNzaW9uQ291bnQgPSAwO1xuICAgIHRoaXMuX3RleHR1cmVDb3VudCA9IDA7XG4gICAgdGhpcy5fbW90aW9uQ291bnQgPSAwO1xuICAgIHRoaXMuX2FsbE1vdGlvbkNvdW50ID0gMDtcbiAgICB0aGlzLl93YXZGaWxlSGFuZGxlciA9IG5ldyBMQXBwV2F2RmlsZUhhbmRsZXIoKTtcblxuICAgIHRoaXMuX2FwcEhvc3RQYXJhbXMgPSB7fTtcbiAgICBpZiAoKHdpbmRvdyBhcyBhbnkpLmFwcEhvc3Qpe1xuICAgICAgKHdpbmRvdyBhcyBhbnkpLmFwcEhvc3Qub24oXCJwYXJhbXNcIiwgKG5ld0FwcEhvc3RQYXJhbXMpPT57XG4gICAgICAgIC8vIGNvbnNvbGUubG9nKG5ld0FwcEhvc3RQYXJhbXMpXG4gICAgICAgIE9iamVjdC5hc3NpZ24odGhpcy5fYXBwSG9zdFBhcmFtcywgbmV3QXBwSG9zdFBhcmFtcyk7XG4gICAgICB9KTtcbiAgICB9XG4gIH1cblxuICBfYXBwSG9zdFBhcmFtczoge1xuICAgIFtpbmRleDpzdHJpbmddOiBhbnksXG4gIH07XG5cbiAgX21vZGVsU2V0dGluZzogSUN1YmlzbU1vZGVsU2V0dGluZzsgLy8g44Oi44OH44Or44K744OD44OG44Kj44Oz44Kw5oOF5aCxXG4gIF9tb2RlbEhvbWVEaXI6IHN0cmluZzsgLy8g44Oi44OH44Or44K744OD44OG44Kj44Oz44Kw44GM572u44GL44KM44Gf44OH44Kj44Os44Kv44OI44OqXG4gIF91c2VyVGltZVNlY29uZHM6IG51bWJlcjsgLy8g44OH44Or44K/5pmC6ZaT44Gu56mN566X5YCkW+enkl1cblxuICBfZXllQmxpbmtJZHM6IGNzbVZlY3RvcjxDdWJpc21JZEhhbmRsZT47IC8vIOODouODh+ODq+OBq+ioreWumuOBleOCjOOBn+eerOOBjeapn+iDveeUqOODkeODqeODoeODvOOCv0lEXG4gIF9saXBTeW5jSWRzOiBjc21WZWN0b3I8Q3ViaXNtSWRIYW5kbGU+OyAvLyDjg6Ljg4fjg6vjgavoqK3lrprjgZXjgozjgZ/jg6rjg4Pjg5fjgrfjg7Pjgq/mqZ/og73nlKjjg5Hjg6njg6Hjg7zjgr9JRFxuXG4gIF9tb3Rpb25zOiBjc21NYXA8c3RyaW5nLCBBQ3ViaXNtTW90aW9uPjsgLy8g6Kqt44G/6L6844G+44KM44Gm44GE44KL44Oi44O844K344On44Oz44Gu44Oq44K544OIXG4gIF9leHByZXNzaW9uczogY3NtTWFwPHN0cmluZywgQUN1YmlzbU1vdGlvbj47IC8vIOiqreOBv+i+vOOBvuOCjOOBpuOBhOOCi+ihqOaDheOBruODquOCueODiFxuXG4gIF9oaXRBcmVhOiBjc21WZWN0b3I8Y3NtUmVjdD47XG4gIF91c2VyQXJlYTogY3NtVmVjdG9yPGNzbVJlY3Q+O1xuXG4gIF9pZFBhcmFtQW5nbGVYOiBDdWJpc21JZEhhbmRsZTsgLy8g44OR44Op44Oh44O844K/SUQ6IFBhcmFtQW5nbGVYXG4gIF9pZFBhcmFtQW5nbGVZOiBDdWJpc21JZEhhbmRsZTsgLy8g44OR44Op44Oh44O844K/SUQ6IFBhcmFtQW5nbGVZXG4gIF9pZFBhcmFtQW5nbGVaOiBDdWJpc21JZEhhbmRsZTsgLy8g44OR44Op44Oh44O844K/SUQ6IFBhcmFtQW5nbGVaXG4gIF9pZFBhcmFtRXllQmFsbFg6IEN1YmlzbUlkSGFuZGxlOyAvLyDjg5Hjg6njg6Hjg7zjgr9JRDogUGFyYW1FeWVCYWxsWFxuICBfaWRQYXJhbUV5ZUJhbGxZOiBDdWJpc21JZEhhbmRsZTsgLy8g44OR44Op44Oh44O844K/SUQ6IFBhcmFtRXllQkFsbFlcbiAgX2lkUGFyYW1Cb2R5QW5nbGVYOiBDdWJpc21JZEhhbmRsZTsgLy8g44OR44Op44Oh44O844K/SUQ6IFBhcmFtQm9keUFuZ2xlWFxuXG4gIF9zdGF0ZTogbnVtYmVyOyAvLyDnj77lnKjjga7jgrnjg4bjg7zjgr/jgrnnrqHnkIbnlKhcbiAgX2V4cHJlc3Npb25Db3VudDogbnVtYmVyOyAvLyDooajmg4Xjg4fjg7zjgr/jgqvjgqbjg7Pjg4hcbiAgX3RleHR1cmVDb3VudDogbnVtYmVyOyAvLyDjg4bjgq/jgrnjg4Hjg6Pjgqvjgqbjg7Pjg4hcbiAgX21vdGlvbkNvdW50OiBudW1iZXI7IC8vIOODouODvOOCt+ODp+ODs+ODh+ODvOOCv+OCq+OCpuODs+ODiFxuICBfYWxsTW90aW9uQ291bnQ6IG51bWJlcjsgLy8g44Oi44O844K344On44Oz57eP5pWwXG4gIF93YXZGaWxlSGFuZGxlcjogTEFwcFdhdkZpbGVIYW5kbGVyOyAvL3dhduODleOCoeOCpOODq+ODj+ODs+ODieODqVxufVxuIiwiX193ZWJwYWNrX3JlcXVpcmVfXy5oID0gZnVuY3Rpb24oKSB7IHJldHVybiBcImIwZTNiNmE4YWYxZGNjNTNkMzkwXCI7IH0iXSwic291cmNlUm9vdCI6IiJ9