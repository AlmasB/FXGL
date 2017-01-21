/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
/**
 * Created at 3:26:14 AM Jan 11, 2011
 */
package org.jbox2d.pooling.normal;

import com.almasb.fxgl.core.math.Vec2;
import org.jbox2d.collision.AABB;
import org.jbox2d.collision.Collision;
import org.jbox2d.collision.Distance;
import org.jbox2d.collision.TimeOfImpact;
import org.jbox2d.common.*;
import org.jbox2d.dynamics.contacts.*;
import org.jbox2d.pooling.IDynamicStack;
import org.jbox2d.pooling.IWorldPool;

import java.util.HashMap;

/**
 * Provides object pooling for all objects used in the engine. Objects retrieved from here should
 * only be used temporarily, and then pushed back (with the exception of arrays).
 *
 * @author Daniel Murphy
 */
public class DefaultWorldPool implements IWorldPool {

    private final OrderedStack<Vec2> vecs;
    private final OrderedStack<Vec3> vec3s;
    private final OrderedStack<Mat22> mats;
    private final OrderedStack<Mat33> mat33s;
    private final OrderedStack<AABB> aabbs;
    private final OrderedStack<Rotation> rots;

    private final HashMap<Integer, float[]> afloats = new HashMap<Integer, float[]>();
    private final HashMap<Integer, int[]> aints = new HashMap<Integer, int[]>();
    private final HashMap<Integer, Vec2[]> avecs = new HashMap<Integer, Vec2[]>();

    private final IWorldPool world = this;

    private final MutableStack<Contact> pcstack =
            new MutableStack<Contact>(JBoxSettings.CONTACT_STACK_INIT_SIZE) {
                protected Contact newInstance() {
                    return new PolygonContact(world);
                }

                protected Contact[] newArray(int size) {
                    return new PolygonContact[size];
                }
            };

    private final MutableStack<Contact> ccstack =
            new MutableStack<Contact>(JBoxSettings.CONTACT_STACK_INIT_SIZE) {
                protected Contact newInstance() {
                    return new CircleContact(world);
                }

                protected Contact[] newArray(int size) {
                    return new CircleContact[size];
                }
            };

    private final MutableStack<Contact> cpstack =
            new MutableStack<Contact>(JBoxSettings.CONTACT_STACK_INIT_SIZE) {
                protected Contact newInstance() {
                    return new PolygonAndCircleContact(world);
                }

                protected Contact[] newArray(int size) {
                    return new PolygonAndCircleContact[size];
                }
            };

    private final MutableStack<Contact> ecstack =
            new MutableStack<Contact>(JBoxSettings.CONTACT_STACK_INIT_SIZE) {
                protected Contact newInstance() {
                    return new EdgeAndCircleContact(world);
                }

                protected Contact[] newArray(int size) {
                    return new EdgeAndCircleContact[size];
                }
            };

    private final MutableStack<Contact> epstack =
            new MutableStack<Contact>(JBoxSettings.CONTACT_STACK_INIT_SIZE) {
                protected Contact newInstance() {
                    return new EdgeAndPolygonContact(world);
                }

                protected Contact[] newArray(int size) {
                    return new EdgeAndPolygonContact[size];
                }
            };

    private final MutableStack<Contact> chcstack =
            new MutableStack<Contact>(JBoxSettings.CONTACT_STACK_INIT_SIZE) {
                protected Contact newInstance() {
                    return new ChainAndCircleContact(world);
                }

                protected Contact[] newArray(int size) {
                    return new ChainAndCircleContact[size];
                }
            };

    private final MutableStack<Contact> chpstack =
            new MutableStack<Contact>(JBoxSettings.CONTACT_STACK_INIT_SIZE) {
                protected Contact newInstance() {
                    return new ChainAndPolygonContact(world);
                }

                protected Contact[] newArray(int size) {
                    return new ChainAndPolygonContact[size];
                }
            };

    private final Collision collision;
    private final TimeOfImpact toi;
    private final Distance dist;

    public DefaultWorldPool(int argSize, int argContainerSize) {
        vecs = new OrderedStack<Vec2>(argSize, argContainerSize) {
            protected Vec2 newInstance() {
                return new Vec2();
            }
        };
        vec3s = new OrderedStack<Vec3>(argSize, argContainerSize) {
            protected Vec3 newInstance() {
                return new Vec3();
            }
        };
        mats = new OrderedStack<Mat22>(argSize, argContainerSize) {
            protected Mat22 newInstance() {
                return new Mat22();
            }
        };
        aabbs = new OrderedStack<AABB>(argSize, argContainerSize) {
            protected AABB newInstance() {
                return new AABB();
            }
        };
        rots = new OrderedStack<Rotation>(argSize, argContainerSize) {
            protected Rotation newInstance() {
                return new Rotation();
            }
        };
        mat33s = new OrderedStack<Mat33>(argSize, argContainerSize) {
            protected Mat33 newInstance() {
                return new Mat33();
            }
        };

        dist = new Distance();
        collision = new Collision(this);
        toi = new TimeOfImpact(this);
    }

    public final IDynamicStack<Contact> getPolyContactStack() {
        return pcstack;
    }

    public final IDynamicStack<Contact> getCircleContactStack() {
        return ccstack;
    }

    public final IDynamicStack<Contact> getPolyCircleContactStack() {
        return cpstack;
    }

    @Override
    public IDynamicStack<Contact> getEdgeCircleContactStack() {
        return ecstack;
    }

    @Override
    public IDynamicStack<Contact> getEdgePolyContactStack() {
        return epstack;
    }

    @Override
    public IDynamicStack<Contact> getChainCircleContactStack() {
        return chcstack;
    }

    @Override
    public IDynamicStack<Contact> getChainPolyContactStack() {
        return chpstack;
    }

    public final Vec2 popVec2() {
        return vecs.pop();
    }

    public final Vec2[] popVec2(int argNum) {
        return vecs.pop(argNum);
    }

    public final void pushVec2(int argNum) {
        vecs.push(argNum);
    }

    public final Vec3 popVec3() {
        return vec3s.pop();
    }

    public final Vec3[] popVec3(int argNum) {
        return vec3s.pop(argNum);
    }

    public final void pushVec3(int argNum) {
        vec3s.push(argNum);
    }

    public final Mat22 popMat22() {
        return mats.pop();
    }

    public final Mat22[] popMat22(int argNum) {
        return mats.pop(argNum);
    }

    public final void pushMat22(int argNum) {
        mats.push(argNum);
    }

    public final Mat33 popMat33() {
        return mat33s.pop();
    }

    public final void pushMat33(int argNum) {
        mat33s.push(argNum);
    }

    public final AABB popAABB() {
        return aabbs.pop();
    }

    public final AABB[] popAABB(int argNum) {
        return aabbs.pop(argNum);
    }

    public final void pushAABB(int argNum) {
        aabbs.push(argNum);
    }

    public final Rotation popRot() {
        return rots.pop();
    }

    public final void pushRot(int num) {
        rots.push(num);
    }

    public final Collision getCollision() {
        return collision;
    }

    public final TimeOfImpact getTimeOfImpact() {
        return toi;
    }

    public final Distance getDistance() {
        return dist;
    }

    public final float[] getFloatArray(int argLength) {
        if (!afloats.containsKey(argLength)) {
            afloats.put(argLength, new float[argLength]);
        }

        assert (afloats.get(argLength).length == argLength) : "Array not built with correct length";
        return afloats.get(argLength);
    }

    public final int[] getIntArray(int argLength) {
        if (!aints.containsKey(argLength)) {
            aints.put(argLength, new int[argLength]);
        }

        assert (aints.get(argLength).length == argLength) : "Array not built with correct length";
        return aints.get(argLength);
    }

    public final Vec2[] getVec2Array(int argLength) {
        if (!avecs.containsKey(argLength)) {
            Vec2[] ray = new Vec2[argLength];
            for (int i = 0; i < argLength; i++) {
                ray[i] = new Vec2();
            }
            avecs.put(argLength, ray);
        }

        assert (avecs.get(argLength).length == argLength) : "Array not built with correct length";
        return avecs.get(argLength);
    }
}
