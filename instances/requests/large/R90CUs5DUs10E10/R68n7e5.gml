graph [
  node [
    id 0
    label "0"
    type 2
    prc 2
  ]
  node [
    id 1
    label "1"
    type 2
    prc 3
  ]
  node [
    id 2
    label "2"
    type 1
    prc 5
    ant 5
    prb 2
    x 19
    y 18
  ]
  node [
    id 3
    label "3"
    type 1
    prc 2
    ant 10
    prb 2
    x 46
    y 101
  ]
  node [
    id 4
    label "4"
    type 1
    prc 4
    ant 9
    prb 5
    x 43
    y 101
  ]
  node [
    id 5
    label "5"
    type 1
    prc 3
    ant 10
    prb 4
    x 48
    y 29
  ]
  node [
    id 6
    label "6"
    type 1
    prc 1
    ant 3
    prb 2
    x 35
    y 72
  ]
  edge [
    source 0
    target 6
    bandwith 995
    delay 415
  ]
  edge [
    source 1
    target 2
    bandwith 204
    delay 296
  ]
  edge [
    source 1
    target 3
    bandwith 393
    delay 175
  ]
  edge [
    source 1
    target 4
    bandwith 504
    delay 313
  ]
  edge [
    source 1
    target 5
    bandwith 185
    delay 383
  ]
]
