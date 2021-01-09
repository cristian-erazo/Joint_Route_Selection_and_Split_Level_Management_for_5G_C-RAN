graph [
  node [
    id 0
    label "0"
    type 2
    prc 4
  ]
  node [
    id 1
    label "1"
    type 2
    prc 4
  ]
  node [
    id 2
    label "2"
    type 2
    prc 5
  ]
  node [
    id 3
    label "3"
    type 2
    prc 5
  ]
  node [
    id 4
    label "4"
    type 2
    prc 2
  ]
  node [
    id 5
    label "5"
    type 1
    prc 4
    ant 7
    prb 6
    x 85
    y 15
  ]
  node [
    id 6
    label "6"
    type 1
    prc 3
    ant 6
    prb 3
    x 53
    y 83
  ]
  node [
    id 7
    label "7"
    type 1
    prc 2
    ant 6
    prb 4
    x 78
    y 109
  ]
  node [
    id 8
    label "8"
    type 1
    prc 2
    ant 2
    prb 2
    x 106
    y 60
  ]
  node [
    id 9
    label "9"
    type 1
    prc 1
    ant 8
    prb 2
    x 66
    y 64
  ]
  node [
    id 10
    label "10"
    type 1
    prc 2
    ant 9
    prb 2
    x 28
    y 101
  ]
  node [
    id 11
    label "11"
    type 1
    prc 1
    ant 2
    prb 5
    x 20
    y 51
  ]
  edge [
    source 0
    target 9
    bandwith 720
    delay 300
  ]
  edge [
    source 1
    target 6
    bandwith 465
    delay 140
  ]
  edge [
    source 2
    target 5
    bandwith 219
    delay 275
  ]
  edge [
    source 3
    target 7
    bandwith 674
    delay 397
  ]
  edge [
    source 3
    target 8
    bandwith 591
    delay 223
  ]
  edge [
    source 4
    target 11
    bandwith 277
    delay 141
  ]
  edge [
    source 4
    target 10
    bandwith 326
    delay 281
  ]
]
